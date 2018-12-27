package co.origon.api.replication;

import co.origon.api.common.Mailer;
import co.origon.api.entities.*;
import com.googlecode.objectify.Key;

import java.util.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class Replicator
{
    private Date dateReplicated = new Date();

    private Set<OReplicatedEntity> entitiesToReplicate;
    private Set<Key<OReplicatedEntity>> entityKeysForDeletion;

    private Map<String, Set<Key<OMembership>>> addedMembershipKeysByProxyId;
    private Map<String, Set<OMembership>> invitedMembershipsByMemberId;
    private Map<String, OMember> touchedMembersByMemberId;
    private Map<String, OMember> modifiedMembersByEmail;

    private Map<Key<OMemberProxy>, OMemberProxy> affectedMemberProxiesByKey;
    private Set<Key<OMemberProxy>> affectedMemberProxyKeys;
    private Set<OMemberProxy> driftingMemberProxies;
    private Set<OMemberProxy> touchedMemberProxies;


    private void processExpiredEntity(OReplicatedEntity expiredEntity)
    {
        if (OReplicatedEntityRef.class.isAssignableFrom(expiredEntity.getClass())) {
            entityKeysForDeletion.add(Key.create(expiredEntity));
        }
    }


    private void processMemberEntity(OMember member, String userEmail)
    {
        touchedMembersByMemberId.put(member.entityId, member);

        String proxyId = member.getProxyId();
        Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);

        if (proxyId.equals(userEmail)) {
            OMemberProxy memberProxy = OMemberProxy.get(userEmail);

            if (memberProxy.getMemberId() == null || memberProxy.getMemberName() == null || !memberProxy.getMemberName().equals(member.name)) {
                if (memberProxy.getMemberId() == null) {
                    memberProxy.setMemberId(member.entityId);
                }

                if (memberProxy.getMemberName() == null || !memberProxy.getMemberName().equals(member.name)) {
                    memberProxy.setMemberName(member.name);
                }

                touchedMemberProxies.add(memberProxy);
            }

            affectedMemberProxiesByKey.put(proxyKey, memberProxy);
        } else {
            if (member.dateReplicated == null) {
                affectedMemberProxiesByKey.put(proxyKey, new OMemberProxy(member));
            } else {
                affectedMemberProxyKeys.add(proxyKey);
            }
        }

        if (member.dateReplicated != null && member.hasEmail()) {
            modifiedMembersByEmail.put(member.email, member);
        }
    }


    private void processMembershipEntity(OMembership membership, String userEmail)
    {
        String proxyId = membership.member.getProxyId();
        Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);

        if (proxyId.equals(userEmail) || membership.dateReplicated == null) {
            if (proxyId.equals(userEmail)) {
                affectedMemberProxiesByKey.put(proxyKey, OMemberProxy.get(userEmail));
            } else {
                affectedMemberProxyKeys.add(proxyKey);

                if (membership.isInvitable()) {
                    Set<OMembership> invitedMemberships = invitedMembershipsByMemberId.computeIfAbsent(membership.member.entityId, k -> new HashSet<>());
                    invitedMemberships.add(membership);
                }
            }

            Set<Key<OMembership>> membershipKeysToAddForMember = addedMembershipKeysByProxyId.computeIfAbsent(proxyId, k -> new HashSet<>());
            membershipKeysToAddForMember.add(Key.create(membership.origoKey, OMembership.class, membership.entityId));
        } else {
            affectedMemberProxyKeys.add(proxyKey);
        }
    }


    private void sendInvitations(OMemberProxy userProxy, Mailer mailer)
    {
        List<Key<OOrigo>> origoKeys = new ArrayList<>();

        for (String memberId : invitedMembershipsByMemberId.keySet()) {
            Set<OMembership> memberships = invitedMembershipsByMemberId.get(memberId);

            for (OMembership membership : memberships) {
                origoKeys.add(Key.create(Key.create(OOrigo.class, membership.origoId), OOrigo.class, membership.origoId));
            }
        }

        Map<String, OOrigo> origosById = new HashMap<>();

        for (OOrigo origo : ofy().load().keys(origoKeys).values()) {
            origosById.put(origo.entityId, origo);
        }

        for (String memberId : invitedMembershipsByMemberId.keySet()) {
            Set<OMembership> memberships = invitedMembershipsByMemberId.get(memberId);
            OMembership invitationMembership = null;
            OOrigo invitationOrigo = null;

            for (OMembership membership : memberships) {
                OOrigo origo = origosById.get(membership.origoId);

                if (origo != null) {
                    if (invitationOrigo == null || origo.takesPrecedenceOver(invitationOrigo)) {
                        invitationMembership = membership;
                        invitationOrigo = origo;
                    }
                }
            }

            if (invitationMembership != null) {
                mailer.sendInvitation(userProxy, invitationMembership, invitationOrigo);
            }
        }
    }


    private void fetchAdditionalAffectedMemberProxies()
    {
        for (String proxyId : addedMembershipKeysByProxyId.keySet()) {
            affectedMemberProxyKeys.add(Key.create(OMemberProxy.class, proxyId));
        }

        if (affectedMemberProxyKeys.size() > 0) {
            affectedMemberProxiesByKey.putAll(ofy().load().keys(affectedMemberProxyKeys));
        }
    }


    private void reanchorDriftingMemberProxies(OMemberProxy userProxy, Mailer mailer)
    {
        Set<String> anchoredProxyIds = new HashSet<>();

        for (OMemberProxy memberProxy : affectedMemberProxiesByKey.values()) {
            anchoredProxyIds.add(memberProxy.getProxyId());
        }

        Set<Key<OMember>> driftingMemberKeys = new HashSet<>();

        for (String email : modifiedMembersByEmail.keySet()) {
            if (!anchoredProxyIds.contains(email)) {
                String memberId = modifiedMembersByEmail.get(email).entityId;
                driftingMemberKeys.add(Key.create(Key.create(OOrigo.class, "~" + memberId), OMember.class, memberId));
            }
        }

        if (driftingMemberKeys.size() > 0) {
            Set<Key<OMemberProxy>> driftingMemberProxyKeys = new HashSet<>();
            Set<OAuthMeta> reanchoredAuthMetaItems = new HashSet<>();

            Collection<OMember> driftingMembers = ofy().load().keys(driftingMemberKeys).values();

            for (OMember driftingMember : driftingMembers) {
                driftingMemberProxyKeys.add(Key.create(OMemberProxy.class, driftingMember.getProxyId()));
            }

            Map<String, OMemberProxy> driftingMemberProxiesByProxyId = new HashMap<>();

            for (OMemberProxy driftingMemberProxy : ofy().load().keys(driftingMemberProxyKeys).values()) {
                driftingMemberProxiesByProxyId.put(driftingMemberProxy.getProxyId(), driftingMemberProxy);
            }

            for (OMember driftingMember : driftingMembers) {
                String driftingMemberProxyId = driftingMember.getProxyId();
                OMember currentMember = touchedMembersByMemberId.get(driftingMember.entityId);

                OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingMemberProxyId);
                OMemberProxy reanchoredMemberProxy = new OMemberProxy(currentMember.email, driftingMemberProxy);

                for (OAuthMeta authMetaItem : ofy().load().keys(reanchoredMemberProxy.getAuthMetaKeys()).values()) {
                    authMetaItem.setEmail(currentMember.email);
                    reanchoredAuthMetaItems.add(authMetaItem);
                }

                driftingMemberProxies.add(driftingMemberProxy);
                touchedMemberProxies.add(reanchoredMemberProxy);
                affectedMemberProxiesByKey.put(Key.create(OMemberProxy.class, currentMember.email), reanchoredMemberProxy);

                if (driftingMember.hasEmail()) {
                    mailer.sendEmailChangeNotification(currentMember, driftingMember.email, userProxy);
                } else {
                    mailer.sendInvitation(currentMember.email, userProxy);
                }
            }

            if (reanchoredAuthMetaItems.size() > 0) {
                ofy().save().entities(reanchoredAuthMetaItems).now();
            }
        }
    }


    private void updateAffectedMembershipKeys()
    {
        for (OMemberProxy memberProxy : affectedMemberProxiesByKey.values()) {
            Set<Key<OMembership>> addedMembershipKeysForMember = addedMembershipKeysByProxyId.get(memberProxy.getProxyId());

            if (addedMembershipKeysForMember != null) {
                memberProxy.getMembershipKeys().addAll(addedMembershipKeysForMember);
                touchedMemberProxies.add(memberProxy);
            }
        }
    }


    public Replicator()
    {
        entitiesToReplicate = new HashSet<>();
        entityKeysForDeletion = new HashSet<>();

        addedMembershipKeysByProxyId = new HashMap<>();
        invitedMembershipsByMemberId = new HashMap<>();
        touchedMembersByMemberId = new HashMap<>();
        modifiedMembersByEmail = new HashMap<>();

        affectedMemberProxiesByKey = new HashMap<>();
        affectedMemberProxyKeys = new HashSet<>();
        driftingMemberProxies = new HashSet<>();
        touchedMemberProxies = new HashSet<>();
    }


    public void replicate(List<OReplicatedEntity> entityList, String userEmail, Mailer mailer)
    {
        for (OReplicatedEntity entity : entityList) {
            entity.origoKey = Key.create(OOrigo.class, entity.origoId);

            if (entity.isExpired) {
                processExpiredEntity(entity);
            } else if (entity.getClass().equals(OMember.class)) {
                processMemberEntity((OMember)entity, userEmail);
            } else if (OMembership.class.isAssignableFrom(entity.getClass())) {
                processMembershipEntity((OMembership)entity, userEmail);
            }

            entity.dateReplicated = dateReplicated;
            entitiesToReplicate.add(entity);
        }

        fetchAdditionalAffectedMemberProxies();
        reanchorDriftingMemberProxies(OMemberProxy.get(userEmail), mailer);
        updateAffectedMembershipKeys();
        sendInvitations(OMemberProxy.get(userEmail), mailer);

        if (touchedMemberProxies.size() > 0) {
            ofy().save().entities(touchedMemberProxies).now();
        }

        if (driftingMemberProxies.size() > 0) {
            ofy().delete().entities(driftingMemberProxies);
        }

        ofy().save().entities(entitiesToReplicate).now();

        if (entityKeysForDeletion.size() > 0) {
            ofy().delete().keys(entityKeysForDeletion);
        }
    }
}
