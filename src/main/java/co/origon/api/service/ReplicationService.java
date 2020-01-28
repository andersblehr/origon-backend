package co.origon.api.service;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.Pair;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMember;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.model.ofy.entity.OReplicatedEntityRef;
import co.origon.api.repository.MemberProxyRepository;
import co.origon.api.repository.MembershipRepository;
import co.origon.api.repository.OrigoRepository;
import com.googlecode.objectify.Key;
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

  @Inject private MemberProxyRepository memberProxyRepository;
  @Inject private MembershipRepository membershipRepository;
  @Inject private OrigoRepository origoRepository;
  @Inject private Mailer rootMailer;

  public void replicate(List<OReplicatedEntity> entities, String userEmail, Language language) {
    final OMemberProxy userProxy = OMemberProxy.get(userEmail);
    final Mailer mailer = rootMailer.using(language);

    final Map<Key<OMemberProxy>, OMember> membersByProxyKey =
        entities.stream()
            .filter(entity -> entity.getClass().equals(OMember.class))
            .map(entity -> (OMember) entity)
            .peek(member -> alignProxyIfUser(member, userProxy))
            .collect(Collectors.toMap(OMember::getProxyKey, member -> member));
    final List<OMembership> memberships =
        entities.stream()
            .filter(entity -> entity.getClass().equals(OMembership.class))
            .map(entity -> (OMembership) entity)
            .collect(Collectors.toList());

    final Map<String, OMemberProxy> proxiesByMemberId =
        processMembers(membersByProxyKey, userProxy, mailer);
    processMemberships(memberships, proxiesByMemberId, userProxy, mailer);

    ofy().save().entities(entities);
    ofy()
        .delete()
        .entities(
            entities.stream()
                .filter(entity -> OReplicatedEntityRef.class.isAssignableFrom(entity.getClass()))
                .filter(entity -> entity.isExpired));
  }

  public List<OReplicatedEntity> fetch(String userEmail) {
    return fetch(userEmail, null);
  }

  public List<OReplicatedEntity> fetch(String userEmail, Date deviceReplicatedAt) {
    return membershipRepository.fetchByKeys(OMemberProxy.get(userEmail).membershipKeys()).stream()
        .flatMap(
            membership ->
                membership.isFetchable()
                    ? fetchMembershipEntities(membership, deviceReplicatedAt)
                    : membership.isExpired ? Stream.of(membership) : Stream.of())
        .distinct()
        .collect(Collectors.toList());
  }

  public List<OReplicatedEntity> lookupMember(String memberId) {
    return Optional.ofNullable(memberProxyRepository.fetchById(memberId))
        .map(
            proxy ->
                membershipRepository.fetchByKeys(proxy.membershipKeys()).stream()
                    .filter(membership -> membership.type.equals("R") && !membership.isExpired)
                    .flatMap(membership -> fetchMembershipEntities(membership, null))
                    .distinct()
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public OOrigo lookupOrigo(String internalJoinCode) {
    return origoRepository.fetchByInternalJoinCode(internalJoinCode);
  }

  private Map<String, OMemberProxy> processMembers(
      Map<Key<OMemberProxy>, OMember> membersByProxyKey, OMemberProxy userProxy, Mailer mailer) {
    final Map<String, OMemberProxy> proxiesByMemberId =
        memberProxyRepository.fetchByKeys(membersByProxyKey.keySet()).stream()
            .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));
    final List<Pair<OMember, Optional<OMemberProxy>>> membersWithMaybeProxy =
        membersByProxyKey.values().stream()
            .map(
                member ->
                    Pair.of(member, Optional.ofNullable(proxiesByMemberId.get(member.entityId))))
            .collect(Collectors.toList());
    final Map<Key<OMemberProxy>, OMember> membersWithEmailChangeByProxyKey =
        membersWithMaybeProxy.stream()
            .filter(pair -> pair.left().hasEmail() && pair.left().isPersisted())
            .filter(pair -> !pair.right().isPresent())
            .collect(Collectors.toMap(pair -> pair.left().getProxyKey(), Pair::left));
    final Map<String, OMemberProxy> staleProxiesById =
        memberProxyRepository.fetchByKeys(membersWithEmailChangeByProxyKey.keySet()).stream()
            .collect(Collectors.toMap(OMemberProxy::proxyId, proxy -> proxy));
    final Map<String, OMemberProxy> updatedProxiesByMemberId =
        membersWithEmailChangeByProxyKey.values().stream()
            .peek(
                member ->
                    reauthorise(member, staleProxiesById.get(member.getProxyId()).authMetaKeys()))
            .peek(
                member ->
                    sendNotification(
                        member,
                        staleProxiesById.get(member.getProxyId()).proxyId(),
                        userProxy,
                        mailer))
            .map(
                member -> new OMemberProxy(member.email, staleProxiesById.get(member.getProxyId())))
            .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));

    memberProxyRepository.deleteByIds(staleProxiesById.keySet());

    return membersWithMaybeProxy.stream()
        .map(pair -> pair.right().orElse(updatedProxiesByMemberId.get(pair.left().entityId)))
        .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));
  }

  private void processMemberships(
      List<OMembership> memberships,
      Map<String, OMemberProxy> proxiesByMemberId,
      OMemberProxy userProxy,
      Mailer mailer) {
    final Set<Key<OOrigo>> origoKeys = new HashSet<>();
    final Map<String, List<OMembership>> invitableMembershipsByMemberId =
        memberships.stream()
            .peek(
                membership ->
                    proxiesByMemberId
                        .get(membership.member.entityId)
                        .membershipKeys()
                        .add(membership.getEntityKey()))
            .filter(membership -> !membership.member.getProxyId().equals(userProxy.proxyId()))
            .filter(OMembership::isInvitable)
            .peek(membership -> origoKeys.add(membership.getOrigoKey()))
            .collect(Collectors.groupingBy(membership -> membership.member.entityId));
    final Map<String, OOrigo> origosById =
        origoRepository.fetchByKeys(origoKeys).stream()
            .collect(Collectors.toMap(origo -> origo.entityId, origo -> origo));

    invitableMembershipsByMemberId.values().stream()
        .map(
            invitableMemberships ->
                invitableMemberships.stream()
                    .reduce(null, (some, other) -> getPrioritised(some, other, origosById)))
        .forEach(
            membership ->
                mailer.sendInvitation(userProxy, membership, origosById.get(membership.origoId)));
  }

  private void alignProxyIfUser(OMember member, OMemberProxy userProxy) {
    if (!member.getProxyId().equals(userProxy.proxyId())) {
      return;
    }
    if (userProxy.memberId() == null) {
      userProxy.memberId(member.entityId);
    }
    if (userProxy.memberName() == null || !userProxy.memberName().equals(member.name)) {
      userProxy.memberName(member.name);
    }
  }

  private void reauthorise(OMember member, Set<Key<OAuthMeta>> authMetaKeys) {
    final Set<OAuthMeta> updatedAuthMetaItems =
        ofy().load().keys(authMetaKeys).values().stream()
            .map(authMetaItem -> authMetaItem.email(member.email))
            .collect(Collectors.toSet());

    ofy().save().entities(updatedAuthMetaItems);
  }

  private void sendNotification(
      OMember member, String oldProxyId, OMemberProxy userProxy, Mailer mailer) {
    if (Matcher.isEmailAddress(oldProxyId)) {
      mailer.sendEmailChangeNotification(member, oldProxyId, userProxy);
    } else {
      mailer.sendInvitation(member.email, userProxy);
    }
  }

  private OMembership getPrioritised(
      OMembership some, OMembership other, Map<String, OOrigo> origosById) {
    if (some == null && other == null) {
      return null;
    }
    if (some == null || other == null) {
      return some == null ? other : some;
    }
    return origosById.get(some.origoId).takesPrecedenceOver(origosById.get(other.origoId))
        ? some
        : other;
  }

  private Stream<OReplicatedEntity> fetchMembershipEntities(
      OMembership membership, Date deviceReplicatedAt) {
    List<OReplicatedEntity> membershipEntities =
        deviceReplicatedAt != null
            ? ofy()
                .load()
                .type(OReplicatedEntity.class)
                .ancestor(membership.parentKey)
                .filter("dateReplicated >", deviceReplicatedAt)
                .list()
            : ofy().load().type(OReplicatedEntity.class).ancestor(membership.parentKey).list();

    return Stream.concat(
        membershipEntities.stream(),
        ofy().load()
            .keys(
                membershipEntities.stream()
                    .filter(entity -> entity.getClass().equals(OReplicatedEntityRef.class))
                    .map(entity -> (OReplicatedEntityRef) entity)
                    .map(entityRef -> entityRef.referencedEntityKey)
                    .collect(Collectors.toSet()))
            .values().stream());
  }
}
