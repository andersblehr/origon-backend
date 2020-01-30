package co.origon.api.service;

import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.Pair;
import co.origon.api.model.EntityKey;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.Entity;
import co.origon.api.model.api.entity.Member;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.model.api.entity.Membership;
import co.origon.api.model.api.entity.Origo;
import co.origon.api.model.api.entity.ReplicatedEntity;
import co.origon.api.model.api.entity.ReplicatedEntityRef;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.repository.api.Repository;
import co.origon.api.repository.api.RepositoryFactory;
import java.time.Instant;
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

  private Repository<MemberProxy> memberProxyRepository;
  private Repository<DeviceCredentials> deviceCredentialsRepository;
  private Repository<Membership> membershipRepository;
  private Repository<Origo> origoRepository;
  private Repository<ReplicatedEntity> entityRepository;

  private Mailer rootMailer;

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
    final MemberProxy userProxy = memberProxyRepository.getById(userEmail).orElse();
    final Mailer mailer = rootMailer.using(language);

    final Map<EntityKey, Member> membersByProxyKey =
        entities.stream()
            .filter(ReplicatedEntity::isMember)
            .map(entity -> (Member) entity)
            .peek(member -> alignProxyIfUser(member, userProxy))
            .collect(Collectors.toMap(member -> EntityKey.from(member.id()), member -> member));
    final List<Membership> memberships =
        entities.stream()
            .filter(ReplicatedEntity::isMembership)
            .map(entity -> (Membership) entity)
            .collect(Collectors.toList());

    final Map<String, MemberProxy> proxiesByMemberId =
        processMembers(membersByProxyKey, userProxy, mailer);
    processMemberships(memberships, proxiesByMemberId, userProxy, mailer);

    entityRepository.save(entities);
    entityRepository.deleteByKeys(
        entities.stream()
            .filter(entity -> entity.isEntityRef() && entity.isExpired())
            .map(ReplicatedEntity::key)
            .collect(Collectors.toList()));
  }

  public List<ReplicatedEntity> fetch(String userEmail) {
    return fetch(userEmail, null);
  }

  public List<ReplicatedEntity> fetch(String userEmail, Date deviceReplicatedAt) {
    return memberProxyRepository
        .getById(userEmail)
        .map(
            proxy ->
                membershipRepository.getByKeys(proxy.membershipKeys()).stream()
                    .flatMap(
                        membership ->
                            membership.isFetchable()
                                ? fetchMembershipEntities(
                                    membership, deviceReplicatedAt.toInstant())
                                : membership.isExpired() ? Stream.of(membership) : Stream.of())
                    .distinct()
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public List<ReplicatedEntity> lookupMember(String memberId) {
    return memberProxyRepository
        .getById(memberId)
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
      Map<EntityKey, Member> membersByProxyKey, MemberProxy userProxy, Mailer mailer) {
    final Map<String, MemberProxy> proxiesByMemberId =
        memberProxyRepository.getByKeys(membersByProxyKey.keySet()).stream()
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
    final List<Pair<Member, Optional<MemberProxy>>> membersWithMaybeProxy =
        membersByProxyKey.values().stream()
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
            .map(member -> new OMemberProxy(member.email(), staleProxiesById.get(member.proxyId())))
            .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));

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

  private void alignProxyIfUser(Member member, MemberProxy userProxy) {
    if (!member.id().equals(userProxy.id())) {
      return;
    }
    if (userProxy.memberId() == null) {
      userProxy.memberId(member.id());
    }
    if (userProxy.memberName() == null || !userProxy.memberName().equals(member.name())) {
      userProxy.memberName(member.name());
    }
  }

  private void reauthorise(Member member, Collection<String> deviceTokens) {
    final Set<DeviceCredentials> updatedDeviceCredentials =
        deviceCredentialsRepository.getByIds(deviceTokens).stream()
            .map(authMetaItem -> authMetaItem.email(member.email()))
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
      Membership membership, Instant deviceReplicatedAt) {
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
