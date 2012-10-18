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
import com.origoapp.api.model.OCachedEntity;
import com.origoapp.api.model.OCachedEntityGhost;
import com.origoapp.api.model.ODevice;
import com.origoapp.api.model.OMember;
import com.origoapp.api.model.OMemberResidency;
import com.origoapp.api.model.OMembership;
import com.origoapp.api.model.OMessageBoard;
import com.origoapp.api.model.OOrigo;
import com.origoapp.api.model.OSharedEntityRef;


public class ODAO extends DAOBase
{
    public OMeta m;

    
    static
    {
        ObjectifyService.register(OAuthInfo.class);
        ObjectifyService.register(OAuthMeta.class);
        
        ObjectifyService.register(OCachedEntityGhost.class);
        ObjectifyService.register(ODevice.class);
        ObjectifyService.register(OMessageBoard.class);
        ObjectifyService.register(OMember.class);
        ObjectifyService.register(OMemberResidency.class);
        ObjectifyService.register(OMembership.class);
        ObjectifyService.register(OOrigo.class);
        ObjectifyService.register(OSharedEntityRef.class);
        
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
    
    
    public void persistEntities(List<OCachedEntity> entityList)
    {
        Set<OCachedEntity> entitiesToPersist = new HashSet<OCachedEntity>();
        
        Set<String> newMemberIds = new HashSet<String>();
        Set<OMemberProxy> memberProxies = new HashSet<OMemberProxy>();
        Map<String, Set<Key<OMembership>>> addedMembershipKeys = new HashMap<String, Set<Key<OMembership>>>();
        
        Set<Key<OCachedEntity>> entityKeysForDeletion = new HashSet<Key<OCachedEntity>>();
        Set<Key<OMemberProxy>> memberProxyKeysForDeletion = new HashSet<Key<OMemberProxy>>();
        
        Date dateModified = new Date();
        
        for (OCachedEntity entity : entityList) {
            entity.origoKey = new Key<OOrigo>(OOrigo.class, entity.origoId);
            
            if (entity.getClass().equals(OCachedEntityGhost.class)) {
                OCachedEntityGhost entityGhost = (OCachedEntityGhost)entity;
                
                if (entityGhost.hasExpired) {
                    entityKeysForDeletion.add(new Key<OCachedEntity>(entity.origoKey, OCachedEntity.class, entity.entityId));
                } else {
                    entitiesToPersist.add(entity);
                    
                    if (entityGhost.ghostedEntityClass.equals(OMember.class.getSimpleName())) {
                        memberProxyKeysForDeletion.add(new Key<OMemberProxy>(OMemberProxy.class, entity.entityId));
                    }
                }
            } else {
                entitiesToPersist.add(entity);
                
                if (entity.getClass().equals(OMember.class) && (entity.dateModified == null)) {
                    newMemberIds.add(entity.entityId);
                    
                    if (entity.entityId.equals(m.getUserId())) {
                        OMemberProxy memberProxy = m.getMemberProxy();
                        
                        memberProxy.didRegister = true;
                        memberProxies.add(memberProxy);
                    } else {
                        memberProxies.add(new OMemberProxy(entity.entityId));
                    }
                } else if (entity.getClass().equals(OMembership.class) || entity.getClass().equals(OMemberResidency.class)) {
                    String memberId = ((OMembership)entity).member.entityId;
                    
                    if (memberId.equals(m.getUserId()) || (entity.dateModified == null)) {
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
            
            entity.dateModified = dateModified;
        }
        
        Set<Key<OMemberProxy>> missingMemberProxyKeys = new HashSet<Key<OMemberProxy>>();
        
        for (String memberId : addedMembershipKeys.keySet()) {
            if (!newMemberIds.contains(memberId)) {
                missingMemberProxyKeys.add(new Key<OMemberProxy>(OMemberProxy.class, memberId));
            }
        }

        if (missingMemberProxyKeys.size() > 0) {
            memberProxies.addAll(ofy().get(missingMemberProxyKeys).values());
        }

        for (OMemberProxy memberProxy : memberProxies) {
            memberProxy.membershipKeys.addAll(addedMembershipKeys.get(memberProxy.userId));
        }
        
        if (memberProxies.size() > 0) {
            ofy().put(memberProxies);
        }
        
        ofy().put(entitiesToPersist);
        OLog.log().fine(m.meta() + "Persisted entities: " + entitiesToPersist.toString());

        if (entityKeysForDeletion.size() > 0) {
            ofy().delete(entityKeysForDeletion);
            OLog.log().fine(m.meta() + "Permanently deleted ghost entities: " + entityKeysForDeletion);
        }
        
        if (memberProxyKeysForDeletion.size() > 0) {
            ofy().delete(memberProxyKeysForDeletion);
            OLog.log().fine(m.meta() + "Deleted member proxies: " + memberProxyKeysForDeletion);
        }
    }
    
    
    public List<OCachedEntity> fetchEntities(Date lastFetchDate)
    {
        OLog.log().fine(m.meta() + "Fetching entities modified since: " + ((lastFetchDate != null) ? lastFetchDate.toString() : "<dawn of time>"));
        
        Collection<OMembership> memberships = ofy().get(m.getMemberProxy().membershipKeys).values();
        
        Set<OCachedEntity> fetchedEntities = new HashSet<OCachedEntity>();
        Set<Key<OCachedEntity>> additionalEntityKeys = new HashSet<Key<OCachedEntity>>();
        
        for (OMembership membership : memberships) {
            if (membership.isActive || (membership.getClass().equals(OMemberResidency.class))) {
                Iterable<OCachedEntity> membershipEntities = null;
                
                if (lastFetchDate != null) {
                    membershipEntities = ofy().query(OCachedEntity.class).ancestor(membership.origoKey).filter("dateModified >", lastFetchDate);
                } else {
                    membershipEntities = ofy().query(OCachedEntity.class).ancestor(membership.origoKey);
                }
                
                for (OCachedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(OSharedEntityRef.class)) {
                        additionalEntityKeys.add(((OSharedEntityRef)entity).sharedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                additionalEntityKeys.add(new Key<OCachedEntity>(membership.origoKey, OCachedEntity.class, membership.origoId));
            }
        }
        
        fetchedEntities.addAll(ofy().get(additionalEntityKeys).values());
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        return new ArrayList<OCachedEntity>(fetchedEntities);
    }
    
    
    public List<OCachedEntity> lookupMember(String memberId)
    {
        OLog.log().fine(m.meta() + "Fetching member with id: " + memberId);
        
        OMemberProxy memberProxy = get(new Key<OMemberProxy>(OMemberProxy.class, memberId));
        ArrayList<OCachedEntity> memberEntities = new ArrayList<OCachedEntity>();
        
        if (memberProxy != null) {
            Set<Key<OCachedEntity>> memberEntityKeys = new HashSet<Key<OCachedEntity>>();
            
            memberEntityKeys.add(memberProxy.memberKey);
            memberEntityKeys.addAll(memberProxy.residencyKeys);
            memberEntityKeys.addAll(memberProxy.residenceKeys);
            
            memberEntities.addAll(ofy().get(memberEntityKeys).values());
        }
        
        return memberEntities;
    }
}
