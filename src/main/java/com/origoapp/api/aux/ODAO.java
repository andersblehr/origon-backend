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
import com.googlecode.objectify.ObjectifyService;

import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.model.OReplicatedEntity;
import com.origoapp.api.model.OReplicatedEntityGhost;
import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMemberResidency;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OMessageBoard;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OReplicatedEntityRef;

import static com.origoapp.api.aux.OObjectifyService.ofy;


public class ODAO
{
    public OMeta m;

    
    static
    {
        ObjectifyService.factory().register(OAuthInfo.class);
        ObjectifyService.factory().register(OAuthMeta.class);
        ObjectifyService.factory().register(OMemberProxy.class);
        
        ObjectifyService.factory().register(ODevice.class);
        ObjectifyService.factory().register(OMember.class);
        ObjectifyService.factory().register(OMemberResidency.class);
        ObjectifyService.factory().register(OMembership.class);
        ObjectifyService.factory().register(OMessageBoard.class);
        ObjectifyService.factory().register(OOrigo.class);
        ObjectifyService.factory().register(OReplicatedEntityGhost.class);
        ObjectifyService.factory().register(OReplicatedEntityRef.class);
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
        
        Collection<OMembership> memberships = ofy().load().keys(m.getMemberProxy().membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        Set<Key<OReplicatedEntity>> additionalEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        
        for (OMembership membership : memberships) {
            if (membership.isActive || (membership.getClass().equals(OMemberResidency.class))) {
                List<OReplicatedEntity> membershipEntities = null;
                
                if (deviceReplicationDate != null) {
                    membershipEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate).list();
                } else {
                    membershipEntities = ofy().load().type(OReplicatedEntity.class).ancestor(membership.origoKey).list();
                }
                
                for (OReplicatedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OReplicatedEntityRef.class)) {
                        additionalEntityKeys.add(((OReplicatedEntityRef)entity).referencedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                fetchedEntities.add(membership);
                additionalEntityKeys.add(Key.create(membership.origoKey, OReplicatedEntity.class, membership.origoId));
            }
        }
        
        fetchedEntities.addAll(ofy().load().keys(additionalEntityKeys).values());
        
        if (m.isAuthenticating()) {
            for (OReplicatedEntity entity : fetchedEntities) {
                if (entity.getClass().equals(OMember.class)) {
                    m.validateIfUser((OMember)entity);
                }
            }
        }
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
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
        
        private Map<String, Set<Key<OMembership>>> membershipKeysToAddByProxyId;
        private Map<String, Set<Key<OMembership>>> membershipKeysToDeleteByProxyId;
        
        private Map<String, OMember> modifiedMembersByMemberId;
        private Map<String, OMember> modifiedMembersByEmail;
        
        private Set<String> addedMemberProxyIds;
        //private Set<Key<OMemberProxy>> affectedMemberProxyKeys;
        private Set<OMemberProxy> affectedMemberProxies;
        
        
        private OEntityReplicator()
        {
            entitiesToReplicate = new HashSet<OReplicatedEntity>();
            entityKeysForDeletion = new HashSet<Key<OReplicatedEntity>>();
            
            membershipKeysToAddByProxyId = new HashMap<String, Set<Key<OMembership>>>();
            membershipKeysToDeleteByProxyId = new HashMap<String, Set<Key<OMembership>>>();
            
            modifiedMembersByMemberId = new HashMap<String, OMember>();
            modifiedMembersByEmail = new HashMap<String, OMember>();
            
            addedMemberProxyIds = new HashSet<String>();
            //affectedMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
            affectedMemberProxies = new HashSet<OMemberProxy>();
        }
        
        
        private void replicate(List<OReplicatedEntity> entityList)
        {
            for (OReplicatedEntity entity : entityList) {
                entity.origoKey = Key.create(OOrigo.class, entity.origoId);
                
                if (entity.getClass().equals(OReplicatedEntityGhost.class)) {
                    _processGhostedEntity((OReplicatedEntityGhost)entity);
                } else {
                    entitiesToReplicate.add(entity);
                    
                    if (entity.getClass().equals(OMember.class)) {
                        _processMemberEntity((OMember)entity);
                    } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                        _processMembershipEntity((OMembership)entity);
                    }
                }
                
                entity.dateReplicated = dateReplicated;
            }
            
            _fetchAdditionalAffectedMemberProxies();
            _reanchorDriftingMemberProxies();
            _updateAffectedEntityReferences();
            _updateAffectedRelationshipKeys();
            
            if (affectedMemberProxies.size() > 0) {
                ofy().save().entities(affectedMemberProxies).now();
            }
            
            ofy().save().entities(entitiesToReplicate).now();
            OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());

            if (entityKeysForDeletion.size() > 0) {
                ofy().delete().keys(entityKeysForDeletion);
                OLog.log().fine(m.meta() + "Permanently deleted entities: " + entityKeysForDeletion);
            }
        }
        
        
        private void _processGhostedEntity(OReplicatedEntityGhost entityGhost)
        {
            if (entityGhost.hasExpired) {
                entityKeysForDeletion.add(Key.create(entityGhost.origoKey, OReplicatedEntity.class, entityGhost.entityId)); // TODO: Decide whether to support expiration
            } else {
                entitiesToReplicate.add(entityGhost);
                
                if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName()) || entityGhost.ghostedEntityClass.equals(OMemberResidency.class.getSimpleName())) {
                    String memberRefId = entityGhost.ghostedMembershipMemberId + "#" + entityGhost.origoId;
                    entityKeysForDeletion.add(Key.create(entityGhost.origoKey, OReplicatedEntity.class, memberRefId));
                    
                    String ghostedMembershipProxyId = (entityGhost.ghostedMembershipMemberEmail != null) ? entityGhost.ghostedMembershipMemberEmail : entityGhost.ghostedMembershipMemberId; 
                    Set<Key<OMembership>> membershipKeysToDeleteForMember = membershipKeysToDeleteByProxyId.get(ghostedMembershipProxyId);
                    
                    if (membershipKeysToDeleteForMember == null) {
                        membershipKeysToDeleteForMember = new HashSet<Key<OMembership>>();
                        membershipKeysToDeleteByProxyId.put(ghostedMembershipProxyId, membershipKeysToDeleteForMember);
                    }
                    
                    membershipKeysToDeleteForMember.add(Key.create(entityGhost.origoKey, OMembership.class, entityGhost.entityId));
                }
            }
        }
        
        
        private void _processMemberEntity(OMember member)
        {
            String proxyId = (member.email != null) ? member.email : member.entityId;
            
            if (member.dateReplicated == null) {
                addedMemberProxyIds.add(proxyId);
                
                if (proxyId.equals(m.getEmail())) {
                    affectedMemberProxies.add(m.getMemberProxy());
                } else {
                    affectedMemberProxies.add(new OMemberProxy(proxyId));
                }
            } else {
                //affectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, proxyId));
                
                modifiedMembersByMemberId.put(member.entityId, member);
                
                if (member.email != null) {
                    modifiedMembersByEmail.put(member.email, member);
                }
            }
        }
        
        
        private void _processMembershipEntity(OMembership membership)
        {
            String proxyId = (membership.member.email != null) ? membership.member.email : membership.member.entityId;
            
            if (proxyId.equals(m.getEmail()) || (membership.dateReplicated == null)) {
                Set<Key<OMembership>> membershipKeysToAddForMember = membershipKeysToAddByProxyId.get(proxyId);
                
                if (membershipKeysToAddForMember == null) {
                    membershipKeysToAddForMember = new HashSet<Key<OMembership>>();
                    membershipKeysToAddByProxyId.put(proxyId, membershipKeysToAddForMember);
                }
                
                membershipKeysToAddForMember.add(Key.create(membership.origoKey, OMembership.class, membership.entityId));
            }
        }
        
        
        private void _fetchAdditionalAffectedMemberProxies()
        {
            Set<Key<OMemberProxy>> affectedMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
            
            for (String memberId : modifiedMembersByMemberId.keySet()) { // Comment out?
                affectedMemberProxyKeys.add(Key.create(OMemberProxy.class, memberId));
            }
            
            for (String proxyId : membershipKeysToAddByProxyId.keySet()) {
                if (!addedMemberProxyIds.contains(proxyId)) {
                    affectedMemberProxyKeys.add(Key.create(OMemberProxy.class, proxyId));
                }
            }
            
            for (String proxyId : membershipKeysToDeleteByProxyId.keySet()) {
                affectedMemberProxyKeys.add(Key.create(OMemberProxy.class, proxyId));
            }

            if (affectedMemberProxyKeys.size() > 0) {
                affectedMemberProxies.addAll(ofy().load().keys(affectedMemberProxyKeys).values());
            }
        }
        
        
        private void _reanchorDriftingMemberProxies()
        {
            Set<String> anchoredProxyIds = new HashSet<String>();
            
            for (OMemberProxy memberProxy : affectedMemberProxies) {
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
                Collection<OMember> driftingMembers = ofy().load().keys(driftingMemberKeys).values();
                
                Set<Key<OMemberProxy>> driftingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
                Set<OAuthMeta> reanchoredAuthMetaItems = new HashSet<OAuthMeta>();
                
                for (OMember member : driftingMembers) {
                    String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                    driftingMemberProxyKeys.add(Key.create(OMemberProxy.class, driftingProxyId));
                }
                
                Map<String, OMemberProxy> driftingMemberProxiesByProxyId = new HashMap<String, OMemberProxy>();
                
                for (OMemberProxy memberProxy : ofy().load().keys(driftingMemberProxyKeys).values()) {
                    driftingMemberProxiesByProxyId.put(memberProxy.proxyId, memberProxy);
                }
                
                Set<OMemberProxy> driftingMemberProxies = new HashSet<OMemberProxy>();
                
                for (OMember member : driftingMembers) {
                    String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                    String modifiedEmail = modifiedMembersByMemberId.get(member.entityId).email; 
                    
                    OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingProxyId);
                    OMemberProxy reanchoredMemberProxy = new OMemberProxy(modifiedEmail, driftingMemberProxy);
                    
                    for (OAuthMeta authMetaItem : ofy().load().keys(reanchoredMemberProxy.authMetaKeys).values()) {
                        authMetaItem.email = modifiedEmail;
                        reanchoredAuthMetaItems.add(authMetaItem);
                    }
                    
                    driftingMemberProxies.add(driftingMemberProxy);
                    affectedMemberProxies.add(reanchoredMemberProxy);
                    
                    if (driftingProxyId.equals(m.getEmail())) {
                        m.setMemberProxy(reanchoredMemberProxy);
                    }
                }
                
                if (reanchoredAuthMetaItems.size() > 0) {
                    ofy().save().entities(reanchoredAuthMetaItems).now();
                }
                
                if (driftingMemberProxies.size() > 0) {
                    ofy().delete().entities(driftingMemberProxies);
                }
            }
        }
        
        
        private void _updateAffectedEntityReferences()
        {
            Map<String, OMemberProxy> affectedMemberProxiesByProxyId = new HashMap<String, OMemberProxy>();
            Set<Key<OReplicatedEntityRef>> affectedEntityRefKeys = new HashSet<Key<OReplicatedEntityRef>>();
            
            for (OMemberProxy memberProxy : affectedMemberProxies) {
                affectedMemberProxiesByProxyId.put(memberProxy.proxyId, memberProxy);
            }
            
            for (OMember member : modifiedMembersByMemberId.values()) {
                String proxyId = (member.email != null) ? member.email : member.entityId;
                OMemberProxy memberProxy = affectedMemberProxiesByProxyId.get(proxyId);
                
                for (Key<OMembership> membershipKey : memberProxy.membershipKeys) {
                    Key<OOrigo> origoKey = membershipKey.getParent();
                    String origoId = origoKey.getRaw().getName();
                    String entityRefId = member.entityId + "#" + origoId;
                    
                    affectedEntityRefKeys.add(Key.create(origoKey, OReplicatedEntityRef.class, entityRefId));
                }
            }
            
            Collection<OReplicatedEntityRef> affectedEntityRefs = ofy().load().keys(affectedEntityRefKeys).values();
            
            for (OReplicatedEntityRef entityRef : affectedEntityRefs) {
                entityRef.dateReplicated = dateReplicated;
            }
            
            entitiesToReplicate.addAll(affectedEntityRefs);
        }
        
        
        private void _updateAffectedRelationshipKeys()
        {
            for (OMemberProxy memberProxy : affectedMemberProxies) {
                Set<Key<OMembership>> membershipKeysToAddForMember = membershipKeysToAddByProxyId.get(memberProxy.proxyId);
                Set<Key<OMembership>> membershipKeysToDeleteForMember = membershipKeysToDeleteByProxyId.get(memberProxy.proxyId);
                
                if (membershipKeysToAddForMember != null) {
                    memberProxy.membershipKeys.addAll(membershipKeysToAddForMember);
                }
                
                if (membershipKeysToDeleteForMember != null) {
                    memberProxy.membershipKeys.removeAll(membershipKeysToDeleteForMember);
                }
            }
        }
    }
}
