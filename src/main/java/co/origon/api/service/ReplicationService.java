package co.origon.api.service;

import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.Pair;
import co.origon.api.model.EntityKey;
import co.origon.api.model.client.Member;
import co.origon.api.model.client.Membership;
import co.origon.api.model.client.Origo;
import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.model.client.ReplicatedEntityRef;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import java.util.Collection;
import java.util.Date;
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
  private final Repository<Member> memberRepository;
  private final Repository<Origo> origoRepository;
  private final Repository<ReplicatedEntity> entityRepository;

  private final Mailer rootMailer;

  @Inject
  public ReplicationService(RepositoryFactory repositoryFactory, Mailer mailer) {
    memberProxyRepository = repositoryFactory.repositoryFor(MemberProxy.class);
    deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
    membershipRepository = repositoryFactory.repositoryFor(Membership.class);
    memberRepository = repositoryFactory.repositoryFor(Member.class);
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

    memberProxyRepository.save(proxiesByMemberId.values());
    entityRepository.save(entities);
    entityRepository.deleteByKeys(
        entities.stream()
            .filter(entity -> entity.isEntityRef() && entity.isExpired())
            .map(ReplicatedEntity::entityKey)
            .collect(Collectors.toList()));
  }

  public Optional<List<ReplicatedEntity>> fetch(String email) {
    return fetch(email, null);
  }

  public Optional<List<ReplicatedEntity>> fetch(String email, Date deviceReplicatedAt) {
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
                    .collect(Collectors.toList()));
  }

  public Optional<List<ReplicatedEntity>> lookupMember(String email) {
    return memberProxyRepository
        .getById(email)
        .map(
            proxy ->
                membershipRepository.getByKeys(proxy.membershipKeys()).stream()
                    .filter(membership -> membership.type().equals("R") && !membership.isExpired())
                    .flatMap(membership -> fetchMembershipEntities(membership, null))
                    .distinct()
                    .collect(Collectors.toList()));
  }

  public Optional<Origo> lookupOrigo(String internalJoinCode) {
    return origoRepository.getByPropertyValue("internalJoinCode", internalJoinCode).stream()
        .findFirst();
  }

  private Map<String, MemberProxy> processMembers(
      Map<String, Member> membersByProxyId, MemberProxy userProxy, Mailer mailer) {

    final Map<String, MemberProxy> proxiesByMemberId =
        memberProxyRepository.getByIds(membersByProxyId.keySet()).stream()
            .map(proxy -> alignProxyIfUser(membersByProxyId.get(proxy.id()), proxy))
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
    final Set<Pair<Member, MemberProxy>> membersWithMaybeProxy =
        membersByProxyId.values().stream()
            .map(member -> Pair.of(member, proxiesByMemberId.get(member.entityId())))
            .collect(Collectors.toSet());
    final Map<EntityKey, Member> membersWithEmailChangeByKey =
        membersWithMaybeProxy.stream()
            .filter(pair -> !pair.right().isPresent())
            .filter(pair -> pair.left().hasEmail() && pair.left().isPersisted())
            .collect(Collectors.toMap(pair -> pair.left().entityKey(), Pair::left));
    final Map<String, Member> membersWithEmailChangeByProxyId =
        memberRepository.getByKeys(membersWithEmailChangeByKey.keySet()).stream()
            .collect(
                Collectors.toMap(
                    Member::proxyId,
                    member -> membersWithEmailChangeByKey.get(member.entityKey())));
    final Map<String, MemberProxy> staleProxiesByMemberId =
        memberProxyRepository.getByIds(membersWithEmailChangeByProxyId.keySet()).stream()
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
    final Map<String, MemberProxy> updatedProxiesByMemberId =
        membersWithEmailChangeByProxyId.values().stream()
            .peek(
                member ->
                    reauthorise(
                        member, staleProxiesByMemberId.get(member.entityId()).deviceTokens()))
            .peek(
                member ->
                    sendNotification(
                        member,
                        staleProxiesByMemberId.get(member.entityId()).id(),
                        userProxy,
                        mailer))
            .map(member -> staleProxiesByMemberId.get(member.proxyId()).withId(member.email()))
            .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));

    memberProxyRepository.deleteByIds(staleProxiesByMemberId.keySet());

    return membersWithMaybeProxy.stream()
        .map(pair -> pair.right().orElse(updatedProxiesByMemberId.get(pair.left().entityId())))
        .collect(Collectors.toMap(MemberProxy::memberId, proxy -> proxy));
  }

  private void processMemberships(
      List<Membership> memberships,
      Map<String, MemberProxy> proxiesByMemberId,
      MemberProxy userProxy,
      Mailer mailer) {

    final Map<EntityKey, List<Membership>> invitableMembershipsByOrigoKey =
        memberships.stream()
            .peek(
                membership ->
                    proxiesByMemberId
                        .get(membership.member().entityId())
                        .membershipKeys()
                        .add(membership.entityKey()))
            .filter(membership -> !membership.member().entityId().equals(userProxy.id()))
            .filter(Membership::isInvitable)
            .collect(Collectors.groupingBy(ReplicatedEntity::origoKey));
    final Map<String, Origo> origosById =
        origoRepository.getByKeys(invitableMembershipsByOrigoKey.keySet()).stream()
            .collect(Collectors.toMap(ReplicatedEntity::entityId, origo -> origo));

    invitableMembershipsByOrigoKey.values().stream()
        .map(
            invitableMemberships ->
                invitableMemberships.stream()
                    .reduce(null, (some, other) -> whittleDown(some, other, origosById)))
        .forEach(
            membership ->
                mailer.sendInvitation(userProxy, membership, origosById.get(membership.origoId())));
  }

  private MemberProxy alignProxyIfUser(Member member, MemberProxy proxy) {
    if (!member.proxyId().equals(proxy.id())) {
      return proxy;
    }
    MemberProxy userProxy = proxy;
    if (userProxy.memberId() == null) {
      userProxy = userProxy.withMemberId(member.entityId());
    }
    if (proxy.memberName() == null || !proxy.memberName().equals(member.name())) {
      userProxy = userProxy.withMemberName(member.name());
    }
    return userProxy;
  }

  private void reauthorise(Member member, Collection<String> deviceTokens) {
    final Set<DeviceCredentials> updatedDeviceCredentials =
        deviceCredentialsRepository.getByIds(deviceTokens).stream()
            .map(deviceCredentials -> deviceCredentials.withEmail(member.email()))
            .collect(Collectors.toSet());

    deviceCredentialsRepository.save(updatedDeviceCredentials);
  }

  private void sendNotification(
      Member member, String maybeStaleEmail, MemberProxy userProxy, Mailer mailer) {
    if (Matcher.isEmailAddress(maybeStaleEmail)) {
      mailer.sendEmailChangeNotification(member, maybeStaleEmail, userProxy);
    } else {
      mailer.sendInvitation(member.email(), userProxy);
    }
  }

  private Membership whittleDown(Membership some, Membership other, Map<String, Origo> origosById) {
    if (some == null || other == null) {
      return some == null ? other : some;
    }
    return origosById.get(some.entityId()).takesPrecedenceOver(origosById.get(other.entityId()))
        ? some
        : other;
  }

  private Stream<ReplicatedEntity> fetchMembershipEntities(
      Membership membership, Date deviceReplicatedAt) {
    Collection<ReplicatedEntity> membershipEntities =
        entityRepository.getByParentId(membership.origoId(), deviceReplicatedAt);
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
