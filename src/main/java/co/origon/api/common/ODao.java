package co.origon.api.common;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.model.ofy.entity.OReplicatedEntityRef;
import co.origon.api.replication.Replicator;
import co.origon.mailer.api.Mailer;
import com.googlecode.objectify.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ODao {
  private Set<Key<OReplicatedEntity>> referencedEntityKeys;

  private Set<OReplicatedEntity> fetchMembershipEntities(
      OMembership membership, Date deviceReplicationDate) {
    List<OReplicatedEntity> fetchedEntities;
    Set<OReplicatedEntity> membershipEntities = new HashSet<>();

    if (deviceReplicationDate != null) {
      fetchedEntities =
          ofy()
              .load()
              .type(OReplicatedEntity.class)
              .ancestor(membership.parentKey)
              .filter("dateReplicated >", deviceReplicationDate)
              .list();
    } else {
      fetchedEntities =
          ofy().load().type(OReplicatedEntity.class).ancestor(membership.parentKey).list();
    }

    for (OReplicatedEntity entity : fetchedEntities) {
      if (entity.getClass().equals(OReplicatedEntityRef.class)) {
        referencedEntityKeys.add(((OReplicatedEntityRef) entity).referencedEntityKey);
      }

      membershipEntities.add(entity);
    }

    return membershipEntities;
  }

  public List<OReplicatedEntity> lookupMemberEntities(String memberId) {
    Set<OReplicatedEntity> memberEntities = new HashSet<>();
    OMemberProxy memberProxy = ofy().load().key(Key.create(OMemberProxy.class, memberId)).now();

    if (memberProxy != null) {
      referencedEntityKeys = new HashSet<>();

      for (OMembership membership : ofy().load().keys(memberProxy.membershipKeys()).values()) {
        if (membership.type.equals("R") && !membership.isExpired) {
          memberEntities.addAll(fetchMembershipEntities(membership, null));
        }
      }

      if (referencedEntityKeys.size() > 0) {
        memberEntities.addAll(ofy().load().keys(referencedEntityKeys).values());
      }
    }

    return memberEntities.size() > 0 ? new ArrayList<>(memberEntities) : null;
  }

  public OOrigo lookupOrigo(String internalJoinCode) {
    OReplicatedEntity origo =
        ofy()
            .load()
            .type(OReplicatedEntity.class)
            .filter("internalJoinCode", internalJoinCode)
            .first()
            .now();

    return (OOrigo) origo;
  }

  public List<OReplicatedEntity> fetchEntities(String userEmail) {
    return fetchEntities(userEmail, null);
  }

  public List<OReplicatedEntity> fetchEntities(String userEmail, Date deviceReplicationDate) {
    referencedEntityKeys = new HashSet<>();

    OMemberProxy memberProxy = OMemberProxy.get(userEmail);
    Collection<OMembership> memberships =
        ofy().load().keys(memberProxy.membershipKeys()).values();

    Set<OReplicatedEntity> fetchedEntities = new HashSet<>();

    for (OMembership membership : memberships) {
      if (membership.isFetchable()) {
        if (deviceReplicationDate == null || membership.dateCreated.after(deviceReplicationDate)) {
          fetchedEntities.addAll(fetchMembershipEntities(membership, null));
        } else {
          fetchedEntities.addAll(fetchMembershipEntities(membership, deviceReplicationDate));
        }
      } else if (membership.isExpired
          && (deviceReplicationDate == null
              || membership.dateReplicated.after(deviceReplicationDate))) {
        fetchedEntities.add(membership);
      }
    }

    if (referencedEntityKeys.size() > 0) {
      fetchedEntities.addAll(ofy().load().keys(referencedEntityKeys).values());
    }

    return new ArrayList<>(fetchedEntities);
  }

  public void replicateEntities(List<OReplicatedEntity> entities, String userEmail, Mailer mailer) {
    if (entities.size() > 0) {
      new Replicator(userEmail, mailer).replicate(entities);
    }
  }
}
