package co.origon.api.service;

import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.Pair;
import co.origon.api.model.Entity;
import co.origon.api.model.EntityKey;
import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.model.client.Member;
import co.origon.api.model.client.Membership;
import co.origon.api.model.client.Origo;
import co.origon.api.model.client.ReplicatedEntityRef;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ReplicationService {

  private final Repository<MemberProxy> memberProxyRepository;
  private final Repository<DeviceCredentials> deviceCredentialsRepository;
  private final Repository<Membership> membershipRepository;
  private final Repository<Origo> origoRepository;
  private final Repository<ReplicatedEntity> entityRepository;

  private final Mailer rootMailer;

  @Inject
  public ReplicationService(RepositoryFactory repositoryFactory, Mailer mailer) {
    memberProxyRepository = repositoryFactory.repositoryFor(MemberProxy.class);
    deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
    membershipRepository = repositoryFactory.repositoryFor(Membership.class);
    origoRepository = repositoryFactory.repositoryFor(Origo.class);
    entityRepository = repositoryFactory.repositoryFor(ReplicatedEntity.class);

    rootMailer = mailer;
  }

  public void replicate(List<ReplicatedEntity> entities, String userEmail, Language language) {
    final MemberProxy userProxy = memberProxyRepository.getById(userEmail).orElse(null); // TODO
    final Mailer mailer = rootMailer.using(language);

    final Map<String, Member> membersByProxyId =
        entities.stream()
            .filter(ReplicatedEntity::isMember)
            .map(entity -> (Member) entity)
            .collect(Collectors.toMap(Member::proxyId, member -> member));
    final List<Membership> memberships =
        entities.stream()
            .filter(ReplicatedEntity::isMembership)
            .map(entity -> (Membership) entity)
            .collect(Collectors.toList());

    final Map<String, MemberProxy> proxiesByMemberId =
        processMembers(membersByProxyId, userProxy, mailer);
    processMemberships(memberships, proxiesByMemberId, userProxy, mailer);

    entityRepository.save(entities);
    entityRepository.deleteByKeys(
        entities.stream()
            .filter(entity -> entity.isEntityRef() && entity.isExpired())
            .map(ReplicatedEntity::key)
            .collect(Collectors.toList()));
  }

  public List<ReplicatedEntity> fetch(String email) {
    return fetch(email, null);
  }

  public List<ReplicatedEntity> fetch(String email, Date deviceReplicatedAt) {
    return memberProxyRepository
        .getById(email)
        .map(
            proxy ->
                membershipRepository.getByKeys(proxy.membershipKeys()).stream()
                    .flatMap(
                        membership ->
                            membership.isFetchable()
                                ? fetchMembershipEntities(membership, deviceReplicatedAt)
                                : membership.isExpired() ? Stream.of(membership) : Stream.of())
                    .distinct()
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public List<ReplicatedEntity> lookupMember(String email) {
    return memberProxyRepository
        .getById(email)
        .map(
            proxy ->
                membershipRepository.getByKeys(proxy.membershipKeys()).stream()
                    .filter(membership -> membership.type().equals("R") && !membership.isExpired())
                    .flatMap(membership -> fetchMembershipEntities(membership, null))
                    .distinct()
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public Origo lookupOrigo(String internalJoinCode) {
    return origoRepository.getByPropertyValue("internalJoinCode", internalJoinCode).stream()
        .findFirst()
        .orElse(null);
  }

  private Map<String, MemberProxy> processMembers(
      Map<String, Member> membersByProxyId, MemberProxy userProxy, Mailer mailer) {
    final Map<String, MemberProxy> proxiesByMemberId =
        memberProxyRepository.getByIds(membersByProxyId.keySet()).stream()
            .map(proxy -> alignedProxyIfUser(membersByProxyId.get(proxy.id()), proxy))
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
    final List<Pair<Member, Optional<MemberProxy>>> membersWithMaybeProxy =
        membersByProxyId.values().stream()
            .map(member -> Pair.of(member, Optional.ofNullable(proxiesByMemberId.get(member.id()))))
            .collect(Collectors.toList());
    final Map<String, Member> membersWithEmailChangeByProxyId =
        membersWithMaybeProxy.stream()
            .filter(pair -> pair.left().hasEmail() && pair.left().isPersisted())
            .filter(pair -> !pair.right().isPresent())
            .collect(Collectors.toMap(pair -> pair.left().proxyId(), Pair::left));
    final Map<String, MemberProxy> staleProxiesById =
        memberProxyRepository.getByIds(membersWithEmailChangeByProxyId.keySet()).stream()
            .collect(Collectors.toMap(MemberProxy::id, proxy -> proxy));
    final Map<String, MemberProxy> updatedProxiesByMemberId =
        membersWithEmailChangeByProxyId.values().stream()
            .peek(
                member ->
                    reauthorise(member, staleProxiesById.get(member.proxyId()).deviceTokens()))
            .peek(
                member ->
                    sendNotification(
                        member, staleProxiesById.get(member.proxyId()).id(), userProxy, mailer))
            .map(member -> staleProxiesById.get(member.proxyId()).withId(member.email()))
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));

    memberProxyRepository.deleteByIds(staleProxiesById.keySet());

    return membersWithMaybeProxy.stream()
        .map(pair -> pair.right().orElse(updatedProxiesByMemberId.get(pair.left().id())))
        .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
  }

  private void processMemberships(
      List<Membership> memberships,
      Map<String, MemberProxy> proxiesByMemberId,
      MemberProxy userProxy,
      Mailer mailer) {
    final Set<EntityKey> origoKeys = new HashSet<>();
    final Map<String, List<Membership>> invitableMembershipsByMemberId =
        memberships.stream()
            .peek(
                membership ->
                    proxiesByMemberId
                        .get(membership.member().id())
                        .membershipKeys()
                        .add(membership.key()))
            .filter(membership -> !membership.member().id().equals(userProxy.id()))
            .filter(Membership::isInvitable)
            .peek(membership -> origoKeys.add(membership.parentKey()))
            .collect(Collectors.groupingBy(membership -> membership.member().id()));
    final Map<String, Origo> origosById =
        origoRepository.getByKeys(origoKeys).stream()
            .collect(Collectors.toMap(Entity::id, origo -> origo));

    invitableMembershipsByMemberId.values().stream()
        .map(
            invitableMemberships ->
                invitableMemberships.stream()
                    .reduce(null, (some, other) -> getPrioritised(some, other, origosById)))
        .forEach(
            membership ->
                mailer.sendInvitation(
                    userProxy, membership, origosById.get(membership.parentId())));
  }

  private MemberProxy alignedProxyIfUser(Member member, MemberProxy proxy) {
    if (!member.proxyId().equals(proxy.id())) {
      return proxy;
    }
    MemberProxy userProxy = proxy;
    if (userProxy.memberId() == null) {
      userProxy = userProxy.withMemberId(member.id());
    }
    if (proxy.memberName() == null || !proxy.memberName().equals(member.name())) {
      userProxy = userProxy.withMemberName(member.name());
    }
    return userProxy;
  }

  private void reauthorise(Member member, Collection<String> deviceTokens) {
    final Set<DeviceCredentials> updatedDeviceCredentials =
        deviceCredentialsRepository.getByIds(deviceTokens).stream()
            .map(deviceCredentials -> deviceCredentials.withUserEmail(member.email()))
            .collect(Collectors.toSet());

    deviceCredentialsRepository.save(updatedDeviceCredentials);
  }

  private void sendNotification(
      Member member, String oldProxyId, MemberProxy userProxy, Mailer mailer) {
    if (Matcher.isEmailAddress(oldProxyId)) {
      mailer.sendEmailChangeNotification(member, oldProxyId, userProxy);
    } else {
      mailer.sendInvitation(member.email(), userProxy);
    }
  }

  private Membership getPrioritised(
      Membership some, Membership other, Map<String, Origo> origosById) {
    if (some == null && other == null) {
      return null;
    }
    if (some == null || other == null) {
      return some == null ? other : some;
    }
    return origosById.get(some.id()).takesPrecedenceOver(origosById.get(other.id())) ? some : other;
  }

  private Stream<ReplicatedEntity> fetchMembershipEntities(
      Membership membership, Date deviceReplicatedAt) {
    Collection<ReplicatedEntity> membershipEntities =
        entityRepository.getByParentId(membership.parentId(), deviceReplicatedAt);
    Collection<ReplicatedEntity> referencedEntities =
        entityRepository.getByKeys(
            membershipEntities.stream()
                .filter(ReplicatedEntity::isEntityRef)
                .map(entity -> (ReplicatedEntityRef) entity)
                .map(ReplicatedEntityRef::referencedEntityKey)
                .collect(Collectors.toSet()));

    return Stream.concat(membershipEntities.stream(), referencedEntities.stream());
  }
}
