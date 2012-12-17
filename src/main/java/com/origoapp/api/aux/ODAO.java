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
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

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


public class ODAO extends DAOBase
{
    public OMeta m;

    
    static
    {
        ObjectifyService.register(OAuthInfo.class);
        ObjectifyService.register(OAuthMeta.class);
        ObjectifyService.register(OMemberProxy.class);
        
        ObjectifyService.register(ODevice.class);
        ObjectifyService.register(OMember.class);
        ObjectifyService.register(OMemberResidency.class);
        ObjectifyService.register(OMembership.class);
        ObjectifyService.register(OMessageBoard.class);
        ObjectifyService.register(OOrigo.class);
        ObjectifyService.register(OReplicatedEntityGhost.class);
        ObjectifyService.register(OReplicatedEntityRef.class);
    }
    
    
    public ODAO(OMeta meta)
    {
        super();
        
        m = meta;
    }
    
    
    public <T> T get(Key<T> key)
    {
        try {
            return ofy().get(key);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    
    public void putAuthToken(String authToken)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        Collection<OAuthMeta> authMetaItems = ofy().get(memberProxy.authMetaKeys).values();
        
        if (authMetaItems.size() > 0) {
            for (OAuthMeta authMeta : authMetaItems) {
                if (authMeta.deviceId.equals(m.getDeviceId())) {
                    memberProxy.authMetaKeys.remove(new Key<OAuthMeta>(OAuthMeta.class, authMeta.authToken));
                    ofy().delete(authMeta);
                    
                    OLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authMeta.authToken, m.getEmail()));
                }
            }
        } else {
            memberProxy.didSignUp = true;
        }
        
        OAuthMeta authMeta = new OAuthMeta(authToken, m.getEmail(), m.getDeviceId(), m.getDeviceType());
        memberProxy.authMetaKeys.add(new Key<OAuthMeta>(OAuthMeta.class, authToken));
        
        ofy().put(authMeta, memberProxy);
        
        OLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getEmail()));
    }
    
    
    public List<OReplicatedEntity> fetchEntities(Date deviceReplicationDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + ((deviceReplicationDate != null) ? deviceReplicationDate.toString() : "<dawn of time>"));
        
        Collection<OMembership> memberships = ofy().get(m.getMemberProxy().membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        Set<Key<OReplicatedEntity>> additionalEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        
        for (OMembership membership : memberships) {
            if (membership.isActive || (membership.getClass().equals(OMemberResidency.class))) {
                Iterable<OReplicatedEntity> membershipEntities = null;
                
                if (deviceReplicationDate != null) {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate);
                } else {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey);
                }
                
                for (OReplicatedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OReplicatedEntityRef.class)) {
                        additionalEntityKeys.add(((OReplicatedEntityRef)entity).referencedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                fetchedEntities.add(membership);
                additionalEntityKeys.add(new Key<OReplicatedEntity>(membership.origoKey, OReplicatedEntity.class, membership.origoId));
            }
        }
        
        fetchedEntities.addAll(ofy().get(additionalEntityKeys).values());
        
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
        //replicateEntities_old(entityList);
    }
    
    
    public void replicateEntities_old(List<OReplicatedEntity> entityList)
    {
        Set<OReplicatedEntity> entitiesToReplicate = new HashSet<OReplicatedEntity>();
        
        Map<String, Set<Key<OMembership>>> membershipKeysToAddByProxyId = new HashMap<String, Set<Key<OMembership>>>();
        Map<String, Set<Key<OMembership>>> membershipKeysToDeleteByProxyId = new HashMap<String, Set<Key<OMembership>>>();
        
        Map<String, OMember> modifiedMembersByMemberId = new HashMap<String, OMember>();
        Map<String, OMember> modifiedMembersByEmail = new HashMap<String, OMember>();
        
        Set<String> addedMemberProxyIds = new HashSet<String>();
        Set<OMemberProxy> affectedMemberProxies = new HashSet<OMemberProxy>();
        Set<Key<OReplicatedEntity>> entityKeysForDeletion = new HashSet<Key<OReplicatedEntity>>();
        
        Date dateReplicated = new Date();
        
        for (OReplicatedEntity entity : entityList) {
            entity.origoKey = new Key<OOrigo>(OOrigo.class, entity.origoId);
            
            if (entity.getClass().equals(OReplicatedEntityGhost.class)) {
                OReplicatedEntityGhost entityGhost = (OReplicatedEntityGhost)entity;
                
                if (entityGhost.hasExpired) {
                    entityKeysForDeletion.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, entityGhost.entityId)); // TODO: Decide whether to support expiration
                } else {
                    entitiesToReplicate.add(entityGhost);
                    
                    if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName()) || entityGhost.ghostedEntityClass.equals(OMemberResidency.class.getSimpleName())) {
                        String memberRefId = String.format("%s#%s", entityGhost.ghostedMembershipMemberId, entityGhost.origoId);
                        entityKeysForDeletion.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, memberRefId));
                        
                        String ghostedMembershipProxyId = (entityGhost.ghostedMembershipMemberEmail != null) ? entityGhost.ghostedMembershipMemberEmail : entityGhost.ghostedMembershipMemberId; 
                        Set<Key<OMembership>> membershipKeysToDeleteForMember = membershipKeysToDeleteByProxyId.get(ghostedMembershipProxyId);
                        
                        if (membershipKeysToDeleteForMember == null) {
                            membershipKeysToDeleteForMember = new HashSet<Key<OMembership>>();
                            membershipKeysToDeleteByProxyId.put(ghostedMembershipProxyId, membershipKeysToDeleteForMember);
                        }
                        
                        membershipKeysToDeleteForMember.add(new Key<OMembership>(entityGhost.origoKey, OMembership.class, entityGhost.entityId));
                    }
                }
            } else {
                entitiesToReplicate.add(entity);
                
                if (entity.getClass().equals(OMember.class)) {
                    OMember member = (OMember)entity;
                    
                    if (entity.dateReplicated == null) {
                        String proxyId = (member.email != null) ? member.email : member.entityId;
                        addedMemberProxyIds.add(proxyId);
                        
                        if (proxyId.equals(m.getEmail())) {
                            affectedMemberProxies.add(m.getMemberProxy());
                        } else {
                            affectedMemberProxies.add(new OMemberProxy(proxyId));
                        }
                    } else {
                        modifiedMembersByMemberId.put(member.entityId, member);
                        
                        if (member.email != null) {
                            modifiedMembersByEmail.put(member.email, member);
                        }
                    }
                } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                    OMember member = ((OMembership)entity).member;
                    String proxyId = (member.email != null) ? member.email : member.entityId;
                    
                    if (proxyId.equals(m.getEmail()) || (entity.dateReplicated == null)) {
                        Set<Key<OMembership>> membershipKeysToAddForMember = membershipKeysToAddByProxyId.get(proxyId);
                        
                        if (membershipKeysToAddForMember == null) {
                            membershipKeysToAddForMember = new HashSet<Key<OMembership>>();
                            membershipKeysToAddByProxyId.put(proxyId, membershipKeysToAddForMember);
                        }
                        
                        membershipKeysToAddForMember.add(new Key<OMembership>(entity.origoKey, OMembership.class, entity.entityId));
                    }
                }
            }
            
            entity.dateReplicated = dateReplicated;
        }
        
        Set<Key<OMemberProxy>> additionalAffectedMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
        
        for (String memberId : modifiedMembersByMemberId.keySet()) {
            additionalAffectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, memberId));
        }
        
        for (String proxyId : membershipKeysToAddByProxyId.keySet()) {
            if (!addedMemberProxyIds.contains(proxyId)) {
                additionalAffectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, proxyId));
            }
        }
        
        for (String proxyId : membershipKeysToDeleteByProxyId.keySet()) {
            additionalAffectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, proxyId));
        }

        if (additionalAffectedMemberProxyKeys.size() > 0) {
            affectedMemberProxies.addAll(ofy().get(additionalAffectedMemberProxyKeys).values());
        }

        // Recreate orphaned member proxies after email change
        
        Set<String> anchoredProxyIds = new HashSet<String>();
        
        for (OMemberProxy memberProxy : affectedMemberProxies) {
            anchoredProxyIds.add(memberProxy.proxyId);
        }
        
        Set<Key<OMember>> driftingMemberKeys = new HashSet<Key<OMember>>();
        
        for (String email : modifiedMembersByEmail.keySet()) {
            if (!anchoredProxyIds.contains(email)) {
                String memberId = modifiedMembersByEmail.get(email).entityId;
                driftingMemberKeys.add(new Key<OMember>(new Key<OOrigo>(OOrigo.class, "~" + memberId), OMember.class, memberId));
            }
        }
        
        if (driftingMemberKeys.size() > 0) {
            Collection<OMember> driftingMembers = ofy().get(driftingMemberKeys).values();
            
            Set<Key<OMemberProxy>> driftingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
            Set<OAuthMeta> reanchoredAuthMetaItems = new HashSet<OAuthMeta>();
            
            for (OMember member : driftingMembers) {
                String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                driftingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, driftingProxyId));
            }
            
            Map<String, OMemberProxy> driftingMemberProxiesByProxyId = new HashMap<String, OMemberProxy>();
            
            for (OMemberProxy memberProxy : ofy().get(driftingMemberProxyKeys).values()) {
                driftingMemberProxiesByProxyId.put(memberProxy.proxyId, memberProxy);
            }
            
            Set<OMemberProxy> driftingMemberProxies = new HashSet<OMemberProxy>();
            
            for (OMember member : driftingMembers) {
                String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                String modifiedEmail = modifiedMembersByMemberId.get(member.entityId).email; 
                
                OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingProxyId);
                OMemberProxy reanchoredMemberProxy = new OMemberProxy(modifiedEmail, driftingMemberProxy);
                
                for (OAuthMeta authMetaItem : ofy().get(reanchoredMemberProxy.authMetaKeys).values()) {
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
                ofy().put(reanchoredAuthMetaItems);
            }
            
            if (driftingMemberProxies.size() > 0) {
                ofy().delete(driftingMemberProxies);
            }
        }
        
        // Update linked references for modified member entities
        
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
                
                affectedEntityRefKeys.add(new Key<OReplicatedEntityRef>(origoKey, OReplicatedEntityRef.class, entityRefId));
            }
        }
        
        Collection<OReplicatedEntityRef> affectedEntityRefs = ofy().get(affectedEntityRefKeys).values();
        
        for (OReplicatedEntityRef entityRef : affectedEntityRefs) {
            entityRef.dateReplicated = dateReplicated;
        }
        
        entitiesToReplicate.addAll(affectedEntityRefs);
        
        // Update member proxy relationship keys
        
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
        
        if (affectedMemberProxies.size() > 0) {
            ofy().put(affectedMemberProxies);
        }
        
        ofy().put(entitiesToReplicate);
        OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());

        if (entityKeysForDeletion.size() > 0) {
            ofy().delete(entityKeysForDeletion);
            OLog.log().fine(m.meta() + "Permanently deleted entities: " + entityKeysForDeletion);
        }
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
            affectedMemberProxies = new HashSet<OMemberProxy>();
        }
        
        
        private void processGhostedEntity(OReplicatedEntityGhost entityGhost)
        {
            if (entityGhost.hasExpired) {
                entityKeysForDeletion.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, entityGhost.entityId)); // TODO: Decide whether to support expiration
            } else {
                entitiesToReplicate.add(entityGhost);
                
                if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName()) || entityGhost.ghostedEntityClass.equals(OMemberResidency.class.getSimpleName())) {
                    String memberRefId = entityGhost.ghostedMembershipMemberId + "#" + entityGhost.origoId;
                    entityKeysForDeletion.add(new Key<OReplicatedEntity>(entityGhost.origoKey, OReplicatedEntity.class, memberRefId));
                    
                    String ghostedMembershipProxyId = (entityGhost.ghostedMembershipMemberEmail != null) ? entityGhost.ghostedMembershipMemberEmail : entityGhost.ghostedMembershipMemberId; 
                    Set<Key<OMembership>> membershipKeysToDeleteForMember = membershipKeysToDeleteByProxyId.get(ghostedMembershipProxyId);
                    
                    if (membershipKeysToDeleteForMember == null) {
                        membershipKeysToDeleteForMember = new HashSet<Key<OMembership>>();
                        membershipKeysToDeleteByProxyId.put(ghostedMembershipProxyId, membershipKeysToDeleteForMember);
                    }
                    
                    membershipKeysToDeleteForMember.add(new Key<OMembership>(entityGhost.origoKey, OMembership.class, entityGhost.entityId));
                }
            }
        }
        
        
        private void processMemberEntity(OMember member)
        {
            if (member.dateReplicated == null) {
                String proxyId = (member.email != null) ? member.email : member.entityId;
                addedMemberProxyIds.add(proxyId);
                
                if (proxyId.equals(m.getEmail())) {
                    affectedMemberProxies.add(m.getMemberProxy());
                } else {
                    affectedMemberProxies.add(new OMemberProxy(proxyId));
                }
            } else {
                modifiedMembersByMemberId.put(member.entityId, member);
                
                if (member.email != null) {
                    modifiedMembersByEmail.put(member.email, member);
                }
            }
        }
        
        
        private void processMembershipEntity(OMembership membership)
        {
            String proxyId = (membership.member.email != null) ? membership.member.email : membership.member.entityId;
            
            if (proxyId.equals(m.getEmail()) || (membership.dateReplicated == null)) {
                Set<Key<OMembership>> membershipKeysToAddForMember = membershipKeysToAddByProxyId.get(proxyId);
                
                if (membershipKeysToAddForMember == null) {
                    membershipKeysToAddForMember = new HashSet<Key<OMembership>>();
                    membershipKeysToAddByProxyId.put(proxyId, membershipKeysToAddForMember);
                }
                
                membershipKeysToAddForMember.add(new Key<OMembership>(membership.origoKey, OMembership.class, membership.entityId));
            }
        }
        
        
        private void fetchAffectedMemberProxies()
        {
            Set<Key<OMemberProxy>> affectedMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
            
            for (String memberId : modifiedMembersByMemberId.keySet()) {
                affectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, memberId));
            }
            
            for (String proxyId : membershipKeysToAddByProxyId.keySet()) {
                if (!addedMemberProxyIds.contains(proxyId)) {
                    affectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, proxyId));
                }
            }
            
            for (String proxyId : membershipKeysToDeleteByProxyId.keySet()) {
                affectedMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, proxyId));
            }

            if (affectedMemberProxyKeys.size() > 0) {
                affectedMemberProxies.addAll(ofy().get(affectedMemberProxyKeys).values());
            }
        }
        
        
        private void reanchorDriftingMemberProxies()
        {
            Set<String> anchoredProxyIds = new HashSet<String>();
            
            for (OMemberProxy memberProxy : affectedMemberProxies) {
                anchoredProxyIds.add(memberProxy.proxyId);
            }
            
            Set<Key<OMember>> driftingMemberKeys = new HashSet<Key<OMember>>();
            
            for (String email : modifiedMembersByEmail.keySet()) {
                if (!anchoredProxyIds.contains(email)) {
                    String memberId = modifiedMembersByEmail.get(email).entityId;
                    driftingMemberKeys.add(new Key<OMember>(new Key<OOrigo>(OOrigo.class, "~" + memberId), OMember.class, memberId));
                }
            }
            
            if (driftingMemberKeys.size() > 0) {
                Collection<OMember> driftingMembers = ofy().get(driftingMemberKeys).values();
                
                Set<Key<OMemberProxy>> driftingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
                Set<OAuthMeta> reanchoredAuthMetaItems = new HashSet<OAuthMeta>();
                
                for (OMember member : driftingMembers) {
                    String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                    driftingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, driftingProxyId));
                }
                
                Map<String, OMemberProxy> driftingMemberProxiesByProxyId = new HashMap<String, OMemberProxy>();
                
                for (OMemberProxy memberProxy : ofy().get(driftingMemberProxyKeys).values()) {
                    driftingMemberProxiesByProxyId.put(memberProxy.proxyId, memberProxy);
                }
                
                Set<OMemberProxy> driftingMemberProxies = new HashSet<OMemberProxy>();
                
                for (OMember member : driftingMembers) {
                    String driftingProxyId = (member.email != null) ? member.email : member.entityId;
                    String modifiedEmail = modifiedMembersByMemberId.get(member.entityId).email; 
                    
                    OMemberProxy driftingMemberProxy = driftingMemberProxiesByProxyId.get(driftingProxyId);
                    OMemberProxy reanchoredMemberProxy = new OMemberProxy(modifiedEmail, driftingMemberProxy);
                    
                    for (OAuthMeta authMetaItem : ofy().get(reanchoredMemberProxy.authMetaKeys).values()) {
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
                    ofy().put(reanchoredAuthMetaItems);
                }
                
                if (driftingMemberProxies.size() > 0) {
                    ofy().delete(driftingMemberProxies);
                }
            }
        }
        
        
        private void updateAffectedEntityReferences()
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
                    
                    affectedEntityRefKeys.add(new Key<OReplicatedEntityRef>(origoKey, OReplicatedEntityRef.class, entityRefId));
                }
            }
            
            Collection<OReplicatedEntityRef> affectedEntityRefs = ofy().get(affectedEntityRefKeys).values();
            
            for (OReplicatedEntityRef entityRef : affectedEntityRefs) {
                entityRef.dateReplicated = dateReplicated;
            }
            
            entitiesToReplicate.addAll(affectedEntityRefs);
        }
        
        
        private void updateAffectedRelationshipKeys()
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
        
        
        protected void replicate(List<OReplicatedEntity> entityList)
        {
            for (OReplicatedEntity entity : entityList) {
                entity.origoKey = new Key<OOrigo>(OOrigo.class, entity.origoId);
                
                if (entity.getClass().equals(OReplicatedEntityGhost.class)) {
                    processGhostedEntity((OReplicatedEntityGhost)entity);
                } else {
                    entitiesToReplicate.add(entity);
                    
                    if (entity.getClass().equals(OMember.class)) {
                        processMemberEntity((OMember)entity);
                    } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                        processMembershipEntity((OMembership)entity);
                    }
                }
                
                entity.dateReplicated = dateReplicated;
            }
            
            fetchAffectedMemberProxies();
            reanchorDriftingMemberProxies();
            updateAffectedEntityReferences();
            updateAffectedRelationshipKeys();
            
            if (affectedMemberProxies.size() > 0) {
                ofy().put(affectedMemberProxies);
            }
            
            ofy().put(entitiesToReplicate);
            OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());

            if (entityKeysForDeletion.size() > 0) {
                ofy().delete(entityKeysForDeletion);
                OLog.log().fine(m.meta() + "Permanently deleted entities: " + entityKeysForDeletion);
            }
        }
    }
}
