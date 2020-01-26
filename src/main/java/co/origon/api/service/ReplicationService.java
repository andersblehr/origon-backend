package co.origon.api.service;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.model.ofy.entity.OReplicatedEntityRef;
import co.origon.api.replication.Replicator;
import co.origon.mailer.api.Mailer;
import com.googlecode.objectify.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;

public class ReplicationService {

  @Inject private Mailer mailer;

  public void replicate(List<OReplicatedEntity> entities, String userEmail, Mailer mailer) {
    if (entities.size() > 0) {
      new Replicator(userEmail, mailer).replicate(entities);
    }
  }

  public List<OReplicatedEntity> fetch(String userEmail) {
    return fetch(userEmail, null);
  }

  public List<OReplicatedEntity> fetch(String userEmail, Date deviceReplicatedAt) {
    return ofy().load().keys(OMemberProxy.get(userEmail).membershipKeys()).values().stream()
        .flatMap(
            membership ->
                membership.isFetchable()
                    ? fetchMembershipEntities(membership, deviceReplicatedAt)
                    : membership.isExpired ? Stream.of(membership) : Stream.of())
        .distinct()
        .collect(Collectors.toList());
  }

  public List<OReplicatedEntity> lookupMember(String memberId) {
    return Optional.ofNullable(ofy().load().key(Key.create(OMemberProxy.class, memberId)).now())
        .map(
            proxy ->
                ofy().load().keys(proxy.membershipKeys()).values().stream()
                    .filter(membership -> membership.type.equals("R") && !membership.isExpired)
                    .flatMap(membership -> fetchMembershipEntities(membership, null))
                    .distinct()
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public OOrigo lookupOrigo(String internalJoinCode) {
    return (OOrigo)
        ofy()
            .load()
            .type(OReplicatedEntity.class)
            .filter("internalJoinCode", internalJoinCode)
            .first()
            .now();
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
