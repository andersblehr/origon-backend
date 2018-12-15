package co.origon.api.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.origon.api.auth.OAuthMeta;
import co.origon.api.model.OMember;
import co.origon.api.model.OMembership;
import co.origon.api.model.OOrigo;
import co.origon.api.model.OReplicatedEntity;
import co.origon.api.model.OReplicatedEntityRef;

import com.googlecode.objectify.Key;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class Dao
{
    private static final Dao dao = new Dao();

    private Set<Key<OReplicatedEntity>> referencedEntityKeys;


    public static Dao getDao() {
        return dao;
    }


    private Set<OReplicatedEntity> fetchMembershipEntities(OMembership membership, Date deviceReplicationDate)
    {
        List<OReplicatedEntity> fetchedEntities;
        Set<OReplicatedEntity> membershipEntities = new HashSet<>();
        
        if (deviceReplicationDate != null) {
            fetchedEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate).list();
        } else {
            fetchedEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).list();
        }
        
        for (OReplicatedEntity entity : fetchedEntities) {
            if (entity.getClass().equals(OReplicatedEntityRef.class)) {
                referencedEntityKeys.add(((OReplicatedEntityRef)entity).referencedEntityKey);
            }
            
            membershipEntities.add(entity);
        }
        
        return membershipEntities;
    }
    
    
    public List<OReplicatedEntity> lookupMemberEntities(String memberId)
    {
        Set<OReplicatedEntity> memberEntities = new HashSet<>();
        OMemberProxy memberProxy = ofy().load().key(Key.create(OMemberProxy.class, memberId)).now(); 
        
        if (memberProxy != null) {
            referencedEntityKeys = new HashSet<>();
            
            for (OMembership membership : ofy().load().keys(memberProxy.membershipKeys).values()) {
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
    
    
    public OOrigo lookupOrigo(String internalJoinCode)
    {
        OReplicatedEntity origo = ofy().load().type(OReplicatedEntity.class).filter("internalJoinCode", internalJoinCode).first().now();
        
        return (OOrigo)origo;
    }


    public List<OReplicatedEntity> fetchEntities(String userEmaiil) {
        return fetchEntities(userEmaiil, null);
    }

    
    public List<OReplicatedEntity> fetchEntities(String userEmaiil, Date deviceReplicationDate)
    {
        referencedEntityKeys = new HashSet<>();
        
        OMemberProxy memberProxy = OMemberProxy.get(userEmaiil);
        Collection<OMembership> memberships = ofy().load().keys(memberProxy.membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<>();
        
        for (OMembership membership : memberships) {
            if (membership.isFetchable()) {
                if (deviceReplicationDate == null || membership.dateCreated.after(deviceReplicationDate)) {
                    fetchedEntities.addAll(fetchMembershipEntities(membership, null));
                } else {
                    fetchedEntities.addAll(fetchMembershipEntities(membership, deviceReplicationDate));
                }
            } else if (membership.isExpired && (deviceReplicationDate == null || membership.dateReplicated.after(deviceReplicationDate))) {
                fetchedEntities.add(membership);
            }
        }
        
        if (referencedEntityKeys.size() > 0) {
            fetchedEntities.addAll(ofy().load().keys(referencedEntityKeys).values());
        }
        
        return new ArrayList<>(fetchedEntities);
    }
    
    
    public void replicateEntities(List<OReplicatedEntity> entityList, String userEmail, Mailer mailer)
    {
        if (entityList.size() > 0) {
            new OEntityReplicator().replicate(entityList, userEmail, mailer);
        }
    }
    
    
    private class OEntityReplicator
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
                
                if (memberProxy.memberId == null || memberProxy.memberName == null || !memberProxy.memberName.equals(member.name)) {
                    if (memberProxy.memberId == null) {
                        memberProxy.memberId = member.entityId;
                    }
                    
                    if (memberProxy.memberName == null || !memberProxy.memberName.equals(member.name)) {
                        memberProxy.memberName = member.name;
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
                        Set<OMembership> invitedMemberships = invitedMembershipsByMemberId.get(membership.member.entityId);
                        
                        if (invitedMemberships == null) {
                            invitedMemberships = new HashSet<>();
                            invitedMembershipsByMemberId.put(membership.member.entityId, invitedMemberships);
                        }
                        
                        invitedMemberships.add(membership);
                    }
                }
                
                Set<Key<OMembership>> membershipKeysToAddForMember = addedMembershipKeysByProxyId.get(proxyId);
                
                if (membershipKeysToAddForMember == null) {
                    membershipKeysToAddForMember = new HashSet<>();
                    addedMembershipKeysByProxyId.put(proxyId, membershipKeysToAddForMember);
                }
                
                membershipKeysToAddForMember.add(Key.create(membership.origoKey, OMembership.class, membership.entityId));
            } else if (membership.dateReplicated != null) {
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
                anchoredProxyIds.add(memberProxy.proxyId);
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
                    driftingMemberProxiesByProxyId.put(driftingMemberProxy.proxyId, driftingMemberProxy);
                }
                
                for (OMember driftingMember : driftingMembers) {
                    String driftingMemberProxyId = driftingMember.getProxyId();
                    OMember currentMember = touchedMembersByMemberId.get(driftingMember.entityId);
                    
                    OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingMemberProxyId);
                    OMemberProxy reanchoredMemberProxy = new OMemberProxy(currentMember.email, driftingMemberProxy);
                    
                    for (OAuthMeta authMetaItem : ofy().load().keys(reanchoredMemberProxy.authMetaKeys).values()) {
                        authMetaItem.email = currentMember.email;
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
                Set<Key<OMembership>> addedMembershipKeysForMember = addedMembershipKeysByProxyId.get(memberProxy.proxyId);
                
                if (addedMembershipKeysForMember != null) {
                    memberProxy.membershipKeys.addAll(addedMembershipKeysForMember);
                    touchedMemberProxies.add(memberProxy);
                }
            }
        }
        
        
        public OEntityReplicator()
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
}
