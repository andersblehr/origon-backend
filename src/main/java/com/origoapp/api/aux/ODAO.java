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
import com.origoapp.api.model.OLinkedEntityRef;


public class ODAO extends DAOBase
{
    public OMeta m;

    
    static
    {
        ObjectifyService.register(OAuthInfo.class);
        ObjectifyService.register(OAuthMeta.class);
        
        ObjectifyService.register(OReplicatedEntityGhost.class);
        ObjectifyService.register(ODevice.class);
        ObjectifyService.register(OMessageBoard.class);
        ObjectifyService.register(OMember.class);
        ObjectifyService.register(OMemberResidency.class);
        ObjectifyService.register(OMembership.class);
        ObjectifyService.register(OOrigo.class);
        ObjectifyService.register(OLinkedEntityRef.class);
        
        ObjectifyService.register(OMemberProxy.class);
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
                    
                    OLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authMeta.authToken, m.getUserId()));
                }
            }
        } else {
            memberProxy.didRegister = true;
        }
        
        OAuthMeta authMeta = new OAuthMeta(authToken, m.getUserId(), m.getDeviceId(), m.getDeviceType());
        memberProxy.authMetaKeys.add(new Key<OAuthMeta>(OAuthMeta.class, authToken));
        
        ofy().put(authMeta, memberProxy);
        
        OLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getUserId()));
    }
    
    
    public void replicateEntities(List<OReplicatedEntity> entityList)
    {
        Set<OReplicatedEntity> entitiesToReplicate = new HashSet<OReplicatedEntity>();
        
        Set<String> addedMemberIds = new HashSet<String>();
        Set<String> modifiedMemberIds = new HashSet<String>();
        Map<String, Set<Key<OMembership>>> addedMembershipKeys = new HashMap<String, Set<Key<OMembership>>>();
        Map<String, Set<Key<OMembership>>> deletedMembershipKeys = new HashMap<String, Set<Key<OMembership>>>();
        
        Set<OMemberProxy> memberProxies = new HashSet<OMemberProxy>();
        Set<Key<OReplicatedEntity>> entityKeysForDeletion = new HashSet<Key<OReplicatedEntity>>();
        
        Date dateReplicated = new Date();
        
        for (OReplicatedEntity entity : entityList) {
            entity.origoKey = new Key<OOrigo>(OOrigo.class, entity.origoId);
            
            if (entity.getClass().equals(OReplicatedEntityGhost.class)) {
                OReplicatedEntityGhost entityGhost = (OReplicatedEntityGhost)entity;
                
                if (entityGhost.hasExpired) {
                    entityKeysForDeletion.add(new Key<OReplicatedEntity>(entity.origoKey, OReplicatedEntity.class, entity.entityId)); // TODO: Decide whether to support expiration
                } else {
                    entitiesToReplicate.add(entity);
                    
                    if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName()) || entityGhost.ghostedEntityClass.equals(OMemberResidency.class.getSimpleName())) {
                        String memberId;
                        
                        if (entityGhost.ghostedEntityClass.equals(OMembership.class.getSimpleName())) {
                            memberId = entityGhost.entityId.substring(0, entityGhost.entityId.indexOf("$"));
                            entityKeysForDeletion.add(new Key<OReplicatedEntity>(entity.origoKey, OReplicatedEntity.class, entityGhost.entityId.replace("$", "#")));
                        } else {
                            memberId = entityGhost.entityId.substring(0, entityGhost.entityId.indexOf("^"));
                            entityKeysForDeletion.add(new Key<OReplicatedEntity>(entity.origoKey, OReplicatedEntity.class, entityGhost.entityId.replace("^", "#")));
                        }
                        
                        modifiedMemberIds.add(memberId);
                        
                        Set<Key<OMembership>> deletedMembershipKeysForMember = deletedMembershipKeys.get(memberId);
                        
                        if (deletedMembershipKeysForMember == null) {
                            deletedMembershipKeysForMember = new HashSet<Key<OMembership>>();
                        }
                        
                        if (deletedMembershipKeysForMember.add(new Key<OMembership>(entityGhost.origoKey, OMembership.class, entityGhost.entityId))) {
                            deletedMembershipKeys.put(memberId, deletedMembershipKeysForMember);
                        }
                    }
                }
            } else {
                entitiesToReplicate.add(entity);
                
                if (entity.getClass().equals(OMember.class) && (entity.dateReplicated == null)) {
                    addedMemberIds.add(entity.entityId);
                    
                    if (entity.entityId.equals(m.getUserId())) {
                        OMemberProxy memberProxy = m.getMemberProxy();
                        
                        memberProxy.didRegister = true;
                        memberProxies.add(memberProxy);
                    } else {
                        memberProxies.add(new OMemberProxy(entity.entityId));
                    }
                } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                    String memberId = ((OMembership)entity).member.entityId;
                    
                    if (memberId.equals(m.getUserId()) || (entity.dateReplicated == null)) {
                        Set<Key<OMembership>> addedMembershipKeysForMember = addedMembershipKeys.get(memberId);
                        
                        if (addedMembershipKeysForMember == null) {
                            addedMembershipKeysForMember = new HashSet<Key<OMembership>>();
                        }
                        
                        if (addedMembershipKeysForMember.add(new Key<OMembership>(entity.origoKey, OMembership.class, entity.entityId))) {
                            addedMembershipKeys.put(memberId, addedMembershipKeysForMember);
                        }
                    }
                }
            }
            
            entity.dateReplicated = dateReplicated;
        }
        
        Set<Key<OMemberProxy>> missingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
        
        for (String memberId : addedMembershipKeys.keySet()) {
            if (!addedMemberIds.contains(memberId)) {
                missingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, memberId));
            }
        }
        
        for (String memberId : deletedMembershipKeys.keySet()) {
            missingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, memberId));
        }

        if (missingMemberProxyKeys.size() > 0) {
            memberProxies.addAll(ofy().get(missingMemberProxyKeys).values());
        }

        for (OMemberProxy memberProxy : memberProxies) {
            Set<Key<OMembership>> addedMembershipKeysForMember = addedMembershipKeys.get(memberProxy.userId);
            
            if (addedMembershipKeysForMember != null) {
                memberProxy.membershipKeys.addAll(addedMembershipKeys.get(memberProxy.userId));
            }
            
            Set<Key<OMembership>> deletedMembershipKeysForMember = deletedMembershipKeys.get(memberProxy.userId);
            
            if (deletedMembershipKeysForMember != null) {
                memberProxy.membershipKeys.removeAll(deletedMembershipKeysForMember);
            }
        }
        
        if (memberProxies.size() > 0) {
            ofy().put(memberProxies);
        }
        
        ofy().put(entitiesToReplicate);
        OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());

        if (entityKeysForDeletion.size() > 0) {
            ofy().delete(entityKeysForDeletion);
            OLog.log().fine(m.meta() + "Permanently deleted entities: " + entityKeysForDeletion);
        }
    }
    
    
    public List<OReplicatedEntity> fetchEntities(Date deviceReplicationDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + ((deviceReplicationDate != null) ? deviceReplicationDate.toString() : "<dawn of time>"));
        
        Collection<OMembership> memberships = ofy().get(m.getMemberProxy().membershipKeys).values();
        
        Set<OReplicatedEntity> fetchedEntities = new HashSet<OReplicatedEntity>();
        Set<Key<OReplicatedEntity>> additionalEntityKeys = new HashSet<Key<OReplicatedEntity>>();
        
        for (OMembership membership : memberships) {
            if (membership.isActive || membership.entityId.startsWith("~") || (membership.getClass().equals(OMemberResidency.class))) {
                Iterable<OReplicatedEntity> membershipEntities = null;
                
                if (deviceReplicationDate != null) {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey).filter("dateReplicated >", deviceReplicationDate);
                } else {
                    membershipEntities = ofy().query(OReplicatedEntity.class).ancestor(membership.origoKey);
                }
                
                for (OReplicatedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OLinkedEntityRef.class)) {
                        additionalEntityKeys.add(((OLinkedEntityRef)entity).linkedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                fetchedEntities.add(membership);
                additionalEntityKeys.add(new Key<OReplicatedEntity>(membership.origoKey, OReplicatedEntity.class, membership.origoId));
            }
        }
        
        fetchedEntities.addAll(ofy().get(additionalEntityKeys).values());
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        return new ArrayList<OReplicatedEntity>(fetchedEntities);
    }
    
    
    public List<OReplicatedEntity> lookupMember(String memberId)
    {
        OLog.log().fine(m.meta() + "Fetching member with id: " + memberId);
        
        ArrayList<OReplicatedEntity> memberEntities = new ArrayList<OReplicatedEntity>();
        OMemberProxy memberProxy = get(new Key<OMemberProxy>(OMemberProxy.class, memberId));
        
        if (memberProxy != null) {
            Set<Key<OReplicatedEntity>> memberEntityKeys = new HashSet<Key<OReplicatedEntity>>();
            
            memberEntityKeys.add(memberProxy.memberKey);
            memberEntityKeys.addAll(memberProxy.residencyKeys);
            memberEntityKeys.addAll(memberProxy.residenceKeys);
            
            memberEntities.addAll(ofy().get(memberEntityKeys).values());
        }
        
        return memberEntities;
    }
}
