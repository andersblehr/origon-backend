package co.origon.api.replication;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.common.Pair;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMember;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.model.ofy.entity.OReplicatedEntityRef;
import co.origon.mailer.api.Mailer;
import com.googlecode.objectify.Key;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Replicator {
  private final String userEmail;
  private final OMemberProxy userProxy;
  private final Mailer mailer;

  public Replicator(String userEmail, Mailer mailer) {
    this.userEmail = userEmail;
    this.userProxy = OMemberProxy.get(userEmail);
    this.mailer = mailer;
  }

  public void replicate(List<OReplicatedEntity> entities) {
    processMemberships(entities, processMembers(entities));

    ofy().save().entities(entities);
    ofy()
        .delete()
        .entities(
            entities.stream()
                .filter(entity -> entity.isExpired)
                .filter(entity -> OReplicatedEntityRef.class.isAssignableFrom(entity.getClass())));
  }

  private Map<String, OMemberProxy> processMembers(List<OReplicatedEntity> entities) {
    final Map<Key<OMemberProxy>, OMember> membersByProxyKey =
        entities.stream()
            .filter(entity -> entity.getClass().equals(OMember.class))
            .map(entity -> (OMember) entity)
            .peek(this::alignProxyIfUser)
            .collect(Collectors.toMap(OMember::getProxyKey, member -> member));
    final Map<String, OMemberProxy> proxiesByMemberId =
        ofy().load().keys(membersByProxyKey.keySet()).values().stream()
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
    final Map<String, OMemberProxy> oldProxiesById =
        ofy().load().keys(membersWithEmailChangeByProxyKey.keySet()).values().stream()
            .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));
    final Map<String, OMemberProxy> updatedProxiesByMemberId =
        membersWithEmailChangeByProxyKey.values().stream()
            .peek(member -> reauthorise(member, oldProxiesById.get(member.entityId).authMetaKeys()))
            .peek(member -> sendNotification(member, oldProxiesById.get(member.entityId).proxyId()))
            .map(member -> new OMemberProxy(member.email, oldProxiesById.get(member.entityId)))
            .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));

    ofy().delete().entities(oldProxiesById.values());

    return membersWithMaybeProxy.stream()
        .map(pair -> pair.right().orElse(updatedProxiesByMemberId.get(pair.left().entityId)))
        .collect(Collectors.toMap(OMemberProxy::memberId, proxy -> proxy));
  }

  private void processMemberships(
      List<OReplicatedEntity> entities, Map<String, OMemberProxy> proxiesByMemberId) {
    final Set<Key<OOrigo>> origoKeys = new HashSet<>();
    final Map<String, List<OMembership>> invitableMembershipsByMemberId =
        entities.stream()
            .filter(entity -> entity.getClass().equals(OMembership.class))
            .map(entity -> (OMembership) entity)
            .peek(
                membership ->
                    proxiesByMemberId
                        .get(membership.member.entityId)
                        .membershipKeys()
                        .add(membership.getEntityKey()))
            .filter(membership -> !membership.member.getProxyId().equals(userEmail))
            .filter(OMembership::isInvitable)
            .peek(membership -> origoKeys.add(membership.getOrigoKey()))
            .collect(Collectors.groupingBy(membership -> membership.member.entityId));
    final Map<String, OOrigo> origosById =
        ofy().load().keys(origoKeys).values().stream()
            .collect(Collectors.toMap(origo -> origo.entityId, origo -> origo));

    invitableMembershipsByMemberId.values().stream()
        .map(
            memberships ->
                memberships.stream()
                    .reduce(null, (some, other) -> getPreceding(some, other, origosById)))
        .forEach(
            membership ->
                mailer.sendInvitation(userProxy, membership, origosById.get(membership.origoId)));
  }

  private void alignProxyIfUser(OMember member) {
    if (!member.getProxyId().equals(userEmail)) {
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

  private void sendNotification(OMember member, String oldEmail) {
    if (member.hasEmail()) {
      mailer.sendEmailChangeNotification(member, oldEmail, userProxy);
    } else {
      mailer.sendInvitation(member.email, userProxy);
    }
  }

  private OMembership getPreceding(
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
}
