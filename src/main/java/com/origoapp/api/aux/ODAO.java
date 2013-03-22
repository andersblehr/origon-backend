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
    public OMeta m;

    
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
            memberProxy.didSignUp = true;
        }
        
        OAuthMeta authMeta = new OAuthMeta(authToken, m.getEmail(), m.getDeviceId(), m.getDeviceType());
        memberProxy.authMetaKeys.add(Key.create(OAuthMeta.class, authToken));
        
        ofy().save().entities(authMeta, memberProxy).now();
        
        OLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getEmail()));
    }
    
    
    public List<OReplicatedEntity> fetchEntities(Date deviceReplicationDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + ((deviceReplicationDate != null) ? deviceReplicationDate.toString() : "<dawn of time>"));
        
        OMemberProxy memberProxy = m.getMemberProxy();
        Collection<OMembership> memberships = ofy().load().keys(memberProxy.membershipKeys).values();
        
        Set<Key<OReplicatedEntity>> additionalEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        
        for (OMembership membership : memberships) {
            if ((membership.isActive || membership.isRootMembership() || membership.isResidency() || membership.isAssociate()) && !membership.isExpired) {
                List<OReplicatedEntity> membershipEntities = null;
                
                if (deviceReplicationDate != null) {
                    membershipEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate).list();
                } else {
                    membershipEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).list();
                }
                
                for (OReplicatedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OReplicatedEntityRef.class)) {
                        additionalEntityKeys.add(((OReplicatedEntityRef)entity).referencedEntityKey);
                    } else {
                        if (entity.isReplicatedForMembership(membership)) {
                            fetchedEntities.add(entity);
                        }
                        
                        if (m.isAuthenticating() && entity.getClass().equals(OMember.class)) {
                            OMember member = (OMember)entity;
                            
                            if ((member.email != null) && member.email.equals(m.getEmail())) {
                                m.setUserId(member.entityId);
                            }
                        }
                    }
                }
            } else {
                fetchedEntities.add(membership);
                
                if (!membership.isExpired) {
                    additionalEntityKeys.add(Key.create(membership.origoKey, OReplicatedEntity.class, membership.origoId));
                }
            }
        }
        
        fetchedEntities.addAll(ofy().load().keys(additionalEntityKeys).values());
        
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
        private Map<String, Set<Key<OMembership>>> revokedMembershipKeysByProxyId;
        
        private Map<String, OMember> touchedMembersByMemberId;
        private Map<String, OMember> modifiedMembersByEmail;
        private Set<OMembership> touchedMemberships;
        
        private Map<Key<OMemberProxy>, OMemberProxy> affectedMemberProxiesByKey;
        private Set<Key<OMemberProxy>> affectedMemberProxyKeys;
        private Set<OMemberProxy> driftingMemberProxies;
        private Set<OMemberProxy> touchedMemberProxies;
        
        
        private void processExpiredEntity(OReplicatedEntity expiredEntity)
        {
            if (OMembership.class.isAssignableFrom(expiredEntity.getClass())) {
                OMembership revokedMembership = (OMembership)expiredEntity;
                
                String revokedMembershipProxyId = revokedMembership.member.getProxyId(); 
                Set<Key<OMembership>> revokedMembershipKeysForMember = revokedMembershipKeysByProxyId.get(revokedMembershipProxyId);
                
                if (revokedMembershipKeysForMember == null) {
                    revokedMembershipKeysForMember = new HashSet<Key<OMembership>>();
                    revokedMembershipKeysByProxyId.put(revokedMembershipProxyId, revokedMembershipKeysForMember);
                }
                
                revokedMembershipKeysForMember.add(Key.create(revokedMembership.origoKey, OMembership.class, revokedMembership.entityId));
            } else if (OReplicatedEntityRef.class.isAssignableFrom(expiredEntity.getClass())) {
                entityKeysForDeletion.add(Key.create(expiredEntity));
            }
        }
        
        
        private void processMemberEntity(OMember member)
        {
            touchedMembersByMemberId.put(member.entityId, member);
            
            String proxyId = member.getProxyId();
            Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);
            
            if (proxyId.equals(m.getEmail())) {
                affectedMemberProxiesByKey.put(proxyKey, m.getMemberProxy());
            } else {
                if (member.dateReplicated == null) {
                    affectedMemberProxiesByKey.put(proxyKey, new OMemberProxy(proxyId));
                } else {
                    affectedMemberProxyKeys.add(proxyKey);
                }
            }
            
            if ((member.dateReplicated != null) && (member.email != null)) {
                modifiedMembersByEmail.put(member.email, member);
            }
        }
        
        
        private void processMembershipEntity(OMembership membership)
        {
            touchedMemberships.add(membership);
            
            String proxyId = membership.member.getProxyId();
            Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);
            
            if (proxyId.equals(m.getEmail()) || (membership.dateReplicated == null)) {
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
                Key<OMemberProxy> proxyKey = Key.create(OMemberProxy.class, proxyId);
                
                if (!affectedMemberProxiesByKey.keySet().contains(proxyKey)) {
                    affectedMemberProxyKeys.add(proxyKey);
                }
            }
            
            for (String proxyId : revokedMembershipKeysByProxyId.keySet()) {
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
                
                for (OMemberProxy memberProxy : ofy().load().keys(driftingMemberProxyKeys).values()) {
                    driftingMemberProxiesByProxyId.put(memberProxy.proxyId, memberProxy);
                }
                
                for (OMember driftingMember : driftingMembers) {
                    String driftingProxyId = driftingMember.getProxyId();
                    String currentEmail = touchedMembersByMemberId.get(driftingMember.entityId).email; 
                    
                    OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingProxyId);
                    OMemberProxy reanchoredMemberProxy = new OMemberProxy(currentEmail, driftingMemberProxy);
                    
                    for (OAuthMeta authMetaItem : ofy().load().keys(reanchoredMemberProxy.authMetaKeys).values()) {
                        authMetaItem.email = currentEmail;
                        reanchoredAuthMetaItems.add(authMetaItem);
                    }
                    
                    driftingMemberProxies.add(driftingMemberProxy);
                    touchedMemberProxies.add(reanchoredMemberProxy);
                    affectedMemberProxiesByKey.put(Key.create(OMemberProxy.class, currentEmail), reanchoredMemberProxy);
                    
                    if (driftingProxyId.equals(m.getEmail())) {
                        m.setMemberProxy(reanchoredMemberProxy);
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
                Set<Key<OMembership>> revokedMembershipKeysForMember = revokedMembershipKeysByProxyId.get(memberProxy.proxyId);
                
                if (addedMembershipKeysForMember != null) {
                    memberProxy.membershipKeys.addAll(addedMembershipKeysForMember);
                    touchedMemberProxies.add(memberProxy);
                }
                
                if (revokedMembershipKeysForMember != null) {
                    memberProxy.membershipKeys.removeAll(revokedMembershipKeysForMember);
                    
                    if (memberProxy.membershipKeys.size() > 1) {
                        touchedMemberProxies.add(memberProxy);
                    } else {
                        Key<OMembership> rootMembershipKey = memberProxy.membershipKeys.iterator().next();
                        String rootOrigoId = rootMembershipKey.getParent().getName();
                        Key<OOrigo> rootOrigoKey = Key.create(OOrigo.class, rootOrigoId);
                        
                        entityKeysForDeletion.add(Key.create(rootOrigoKey, OReplicatedEntity.class, rootMembershipKey.getName()));
                        entityKeysForDeletion.add(Key.create(rootOrigoKey, OReplicatedEntity.class, rootOrigoId));
                        entityKeysForDeletion.add(Key.create(rootOrigoKey, OReplicatedEntity.class, rootOrigoId.substring(1)));
                        
                        driftingMemberProxies.add(memberProxy);
                    }
                }
            }
        }
        
        
        private void updateAffectedEntityReferences()
        {
            Set<Key<OReplicatedEntityRef>> affectedEntityRefKeys = new HashSet<Key<OReplicatedEntityRef>>();
            
            for (OMember member : touchedMembersByMemberId.values()) {
                OMemberProxy memberProxy = affectedMemberProxiesByKey.get(Key.create(OMemberProxy.class, member.getProxyId()));
                
                for (Key<OMembership> membershipKey : memberProxy.membershipKeys) {
                    Key<OOrigo> origoKey = membershipKey.getParent();
                    String origoId = origoKey.getRaw().getName();
                    String entityRefId = member.entityId + "#" + origoId;
                    
                    affectedEntityRefKeys.add(Key.create(origoKey, OReplicatedEntityRef.class, entityRefId));
                }
            }
            
            for (OMembership membership : touchedMemberships) {
                if (!membership.isRootMembership()) {
                    OMemberProxy memberProxy = affectedMemberProxiesByKey.get(Key.create(OMemberProxy.class, membership.member.getProxyId()));
                    
                    for (Key<OMembership> membershipKey : memberProxy.membershipKeys) {
                        Key<OOrigo> origoKey = membershipKey.getParent();
                        String origoId = origoKey.getRaw().getName();
                        String entityRefId = membership.entityId + "#" + origoId;
                        
                        affectedEntityRefKeys.add(Key.create(origoKey, OReplicatedEntityRef.class, entityRefId));
                    }
                }
            }
            
            Collection<OReplicatedEntityRef> affectedEntityRefs = ofy().load().keys(affectedEntityRefKeys).values();
            
            for (OReplicatedEntityRef entityRef : affectedEntityRefs) {
                entityRef.dateReplicated = dateReplicated;
            }
            
            entitiesToReplicate.addAll(affectedEntityRefs);
        }
        
        
        public OEntityReplicator()
        {
            entitiesToReplicate = new HashSet<OReplicatedEntity>();
            entityKeysForDeletion = new HashSet<Key<OReplicatedEntity>>();
            
            addedMembershipKeysByProxyId = new HashMap<String, Set<Key<OMembership>>>();
            revokedMembershipKeysByProxyId = new HashMap<String, Set<Key<OMembership>>>();
            
            touchedMembersByMemberId = new HashMap<String, OMember>();
            modifiedMembersByEmail = new HashMap<String, OMember>();
            touchedMemberships = new HashSet<OMembership>();
            
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
            updateAffectedEntityReferences();
            
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
