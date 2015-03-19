package com.origoapp.api.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.model.OReplicatedEntity;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OReplicatedEntityRef;

import static com.origoapp.api.aux.OObjectifyService.ofy;


public class ODAO
{
    private Set<Key<OReplicatedEntity>> referencedEntityKeys;
    
    public OMeta m;

    
    private Set<OReplicatedEntity> fetchMembershipEntities(OMembership membership, Date deviceReplicationDate)
    {
        List<OReplicatedEntity> fetchedEntities = null;
        Set<OReplicatedEntity> membershipEntities = new HashSet<OReplicatedEntity>();
        
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
    
    
    public ODAO(OMeta meta)
    {
        super();
        
        m = meta;
    }
    
    
    public void putAuthToken(String authToken)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        Collection<OAuthMeta> authMetaItems = ofy().load().keys(memberProxy.authMetaKeys).values();
        
        if (authMetaItems.size() > 0) {
            for (OAuthMeta authMeta : authMetaItems) {
                if (authMeta.deviceId.equals(m.getDeviceId())) {
                    memberProxy.authMetaKeys.remove(Key.create(OAuthMeta.class, authMeta.authToken));
                    ofy().delete().entity(authMeta);
                    
                    OLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authMeta.authToken, m.getEmail()));
                }
            }
        } else {
            memberProxy.didRegister = true;
        }
        
        OAuthMeta authMeta = new OAuthMeta(authToken, m.getEmail(), m.getDeviceId(), m.getDeviceType());
        memberProxy.authMetaKeys.add(Key.create(OAuthMeta.class, authToken));
        
        ofy().save().entities(authMeta, memberProxy).now();
        
        OLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getEmail()));
    }
    
    
    public List<OReplicatedEntity> lookupMemberEntities(String memberId)
    {
        OLog.log().fine(m.meta() + "Fetching member with id: " + memberId);
        
        Set<OReplicatedEntity> memberEntities = new HashSet<OReplicatedEntity>();
        OMemberProxy memberProxy = ofy().load().key(Key.create(OMemberProxy.class, memberId)).now(); 
        
        if (memberProxy != null) {
            referencedEntityKeys = new HashSet<Key<OReplicatedEntity>>();
            
            for (OMembership membership : ofy().load().keys(memberProxy.membershipKeys).values()) {
                if (membership.type.equals("R") && !membership.isExpired) {
                    memberEntities.addAll(fetchMembershipEntities(membership, null));
                }
            }
            
            if (referencedEntityKeys.size() > 0) {
                memberEntities.addAll(ofy().load().keys(referencedEntityKeys).values());
            }
        }
        
        return memberEntities.size() > 0 ? new ArrayList<OReplicatedEntity>(memberEntities) : null;
    }
    
    
    public OOrigo lookupOrigo(String internalJoinCode)
    {
        OLog.log().fine(m.meta() + "Looking up origo with internal join code: " + internalJoinCode);
        
        OReplicatedEntity origo = ofy().load().type(OReplicatedEntity.class).filter("internalJoinCode", internalJoinCode).first().now();
        
        return (OOrigo)origo;
    }
    
    
    public List<OReplicatedEntity> fetchEntities(Date deviceReplicationDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + (deviceReplicationDate != null ? deviceReplicationDate.toString() : "<dawn of time>"));
        
        referencedEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        
        OMemberProxy memberProxy = m.getMemberProxy();
        Collection<OMembership> memberships = ofy().load().keys(memberProxy.membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        
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
        
        return new ArrayList<OReplicatedEntity>(fetchedEntities);
    }
    
    
    public void replicateEntities(List<OReplicatedEntity> entityList)
    {
        new OEntityReplicator().replicate(entityList);
    }
    
    
    private class OEntityReplicator
    {
        private Date dateReplicated = new Date();
        
        private Set<OReplicatedEntity> entitiesToReplicate;
        private Set<Key<OReplicatedEntity>> entityKeysForDeletion;
        
        private Map<String, Set<Key<OMembership>>> addedMembershipKeysByProxyId;
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
        
        
        private void processMemberEntity(OMember member)
        {
            touchedMembersByMemberId.put(member.entityId, member);
            
            String proxyId = member.getProxyId();
            Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);
            
            if (proxyId.equals(m.getEmail())) {
                OMemberProxy memberProxy = m.getMemberProxy();
                
                if (memberProxy.memberId == null) {
                    memberProxy.memberId = member.entityId;
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
        
        
        private void processMembershipEntity(OMembership membership)
        {
            String proxyId = membership.member.getProxyId();
            Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);
            
            if (proxyId.equals(m.getEmail()) || membership.dateReplicated == null) {
                if (proxyId.equals(m.getEmail())) {
                    affectedMemberProxiesByKey.put(proxyKey, m.getMemberProxy());
                } else {
                    affectedMemberProxyKeys.add(proxyKey);
                }
                
                Set<Key<OMembership>> membershipKeysToAddForMember = addedMembershipKeysByProxyId.get(proxyId);
                
                if (membershipKeysToAddForMember == null) {
                    membershipKeysToAddForMember = new HashSet<Key<OMembership>>();
                    addedMembershipKeysByProxyId.put(proxyId, membershipKeysToAddForMember);
                }
                
                membershipKeysToAddForMember.add(Key.create(membership.origoKey, OMembership.class, membership.entityId));
            } else if (membership.dateReplicated != null) {
                affectedMemberProxyKeys.add(proxyKey);
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
        
        
        private void reanchorDriftingMemberProxies()
        {
            Set<String> anchoredProxyIds = new HashSet<String>();
            
            for (OMemberProxy memberProxy : affectedMemberProxiesByKey.values()) {
                anchoredProxyIds.add(memberProxy.proxyId);
            }
            
            Set<Key<OMember>> driftingMemberKeys = new HashSet<Key<OMember>>();
            
            for (String email : modifiedMembersByEmail.keySet()) {
                if (!anchoredProxyIds.contains(email)) {
                    String memberId = modifiedMembersByEmail.get(email).entityId;
                    driftingMemberKeys.add(Key.create(Key.create(OOrigo.class, "~" + memberId), OMember.class, memberId));
                }
            }
            
            if (driftingMemberKeys.size() > 0) {
                Set<Key<OMemberProxy>> driftingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
                Set<OAuthMeta> reanchoredAuthMetaItems = new HashSet<OAuthMeta>();
                
                Collection<OMember> driftingMembers = ofy().load().keys(driftingMemberKeys).values();
                
                for (OMember driftingMember : driftingMembers) {
                    driftingMemberProxyKeys.add(Key.create(OMemberProxy.class, driftingMember.getProxyId()));
                }
                
                Map<String, OMemberProxy> driftingMemberProxiesByProxyId = new HashMap<String, OMemberProxy>();
                
                for (OMemberProxy driftingMemberProxy : ofy().load().keys(driftingMemberProxyKeys).values()) {
                    driftingMemberProxiesByProxyId.put(driftingMemberProxy.proxyId, driftingMemberProxy);
                }
                
                for (OMember driftingMember : driftingMembers) {
                    String driftingMemberProxyId = driftingMember.getProxyId();
                    String currentEmail = touchedMembersByMemberId.get(driftingMember.entityId).email; 
                    
                    OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingMemberProxyId);
                    OMemberProxy reanchoredMemberProxy = new OMemberProxy(currentEmail, driftingMemberProxy);
                    
                    for (OAuthMeta authMetaItem : ofy().load().keys(reanchoredMemberProxy.authMetaKeys).values()) {
                        authMetaItem.email = currentEmail;
                        reanchoredAuthMetaItems.add(authMetaItem);
                    }
                    
                    driftingMemberProxies.add(driftingMemberProxy);
                    touchedMemberProxies.add(reanchoredMemberProxy);
                    affectedMemberProxiesByKey.put(Key.create(OMemberProxy.class, currentEmail), reanchoredMemberProxy);
                    
                    if (driftingMemberProxyId.equals(m.getEmail())) {
                        m.setMemberProxy(reanchoredMemberProxy);
                    } else {
                        OLog.log().fine(m.meta() + "TODO: Send notification email to new address.");
                        // TODO: Send notification email (new address: currentEmail; old address: driftingMember.getProxyId())
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
            entitiesToReplicate = new HashSet<OReplicatedEntity>();
            entityKeysForDeletion = new HashSet<Key<OReplicatedEntity>>();
            
            addedMembershipKeysByProxyId = new HashMap<String, Set<Key<OMembership>>>();
            touchedMembersByMemberId = new HashMap<String, OMember>();
            modifiedMembersByEmail = new HashMap<String, OMember>();
            
            affectedMemberProxiesByKey = new HashMap<Key<OMemberProxy>, OMemberProxy>();
            affectedMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
            driftingMemberProxies = new HashSet<OMemberProxy>();
            touchedMemberProxies = new HashSet<OMemberProxy>();
        }
        
        
        public void replicate(List<OReplicatedEntity> entityList)
        {
            for (OReplicatedEntity entity : entityList) {
                entity.origoKey = Key.create(OOrigo.class, entity.origoId);
                
                if (entity.isExpired) {
                    processExpiredEntity(entity);
                } else if (entity.getClass().equals(OMember.class)) {
                    processMemberEntity((OMember)entity);
                } else if (OMembership.class.isAssignableFrom(entity.getClass())) {
                    processMembershipEntity((OMembership)entity);
                }
                
                entity.dateReplicated = dateReplicated;
                entitiesToReplicate.add(entity);
            }
            
            fetchAdditionalAffectedMemberProxies();
            reanchorDriftingMemberProxies();
            updateAffectedMembershipKeys();
            
            if (touchedMemberProxies.size() > 0) {
                ofy().save().entities(touchedMemberProxies).now();
            }
            
            if (driftingMemberProxies.size() > 0) {
                ofy().delete().entities(driftingMemberProxies);
            }
            
            ofy().save().entities(entitiesToReplicate).now();

            if (entityKeysForDeletion.size() > 0) {
                ofy().delete().keys(entityKeysForDeletion);
                OLog.log().fine(m.meta() + "Permanently deleted entities: " + entityKeysForDeletion);
            }
        }
    }
}
