package com.scolaapp.api.aux;

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

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthMeta;
import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScMemberResidency;
import com.scolaapp.api.model.ScMessageBoard;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScMember;
import com.scolaapp.api.model.ScMembership;
import com.scolaapp.api.model.ScSharedEntityRef;
import com.scolaapp.api.model.proxy.ScMemberProxy;


public class ScDAO extends DAOBase
{
    public ScMeta m;

    
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        ObjectifyService.register(ScAuthMeta.class);
        
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScMessageBoard.class);
        ObjectifyService.register(ScMember.class);
        ObjectifyService.register(ScMemberResidency.class);
        ObjectifyService.register(ScMembership.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScSharedEntityRef.class);
        
        ObjectifyService.register(ScMemberProxy.class);
    }
    
    
    public ScDAO(ScMeta meta)
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
        ScMemberProxy memberProxy = m.getMemberProxy();
        
        Collection<ScAuthMeta> authMetaItems = ofy().get(memberProxy.authMetaKeys).values();
        
        for (ScAuthMeta authMeta : authMetaItems) {
            if (authMeta.deviceId.equals(m.getDeviceId())) {
                memberProxy.authMetaKeys.remove(new Key<ScAuthMeta>(ScAuthMeta.class, authMeta.authToken));
                ofy().delete(authMeta);
                
                ScLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authMeta.authToken, m.getUserId()));
            }
        }
        
        memberProxy.authMetaKeys.add(new Key<ScAuthMeta>(ScAuthMeta.class, authToken));
        ScAuthMeta authMeta = new ScAuthMeta(authToken, m.getUserId(), m.getScolaId(), m.getDeviceId(), m.getDeviceType());
        
        ofy().put(memberProxy, authMeta);
        
        ScLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getUserId()));
    }
    
    
    public void persistEntities(List<ScCachedEntity> entityList)
    {
        Set<ScCachedEntity> entities = new HashSet<ScCachedEntity>(entityList);
        
        Set<String> memberIds = new HashSet<String>();
        Set<ScMemberProxy> memberProxies = new HashSet<ScMemberProxy>();
        Map<String, Set<Key<ScMembership>>> addedMembershipKeys = new HashMap<String, Set<Key<ScMembership>>>();
        
        Date now = new Date();
        
        for (ScCachedEntity entity : entities) {
            entity.scolaKey = new Key<ScScola>(ScScola.class, entity.scolaId);
            
            if (entity.getClass().equals(ScMember.class) && (entity.dateModified == null)) {
                memberIds.add(entity.entityId);
                
                if (entity.entityId.equals(m.getUserId())) {
                    memberProxies.add(m.getMemberProxy());
                } else {
                    memberProxies.add(new ScMemberProxy(entity.entityId, entity.scolaId));
                }
            } else if (entity.getClass().equals(ScMembership.class) || entity.getClass().equals(ScMemberResidency.class)) {
                String memberId = ((ScMembership)entity).member.entityId;
                
                if (memberId.equals(m.getUserId()) || (entity.dateModified == null)) {
                    Set<Key<ScMembership>> addedMembershipKeysForMember = addedMembershipKeys.get(memberId);
                    
                    if (addedMembershipKeysForMember == null) {
                        addedMembershipKeysForMember = new HashSet<Key<ScMembership>>();
                    }
                    
                    if (addedMembershipKeysForMember.add(new Key<ScMembership>(entity.scolaKey, ScMembership.class, entity.entityId))) {
                        addedMembershipKeys.put(memberId, addedMembershipKeysForMember);
                    }
                }
            }
            
            entity.dateModified = now;
        }
        
        Set<Key<ScMemberProxy>> missingMemberProxyKeys = new HashSet<Key<ScMemberProxy>>();
        
        for (String memberId : addedMembershipKeys.keySet()) {
            if (!memberIds.contains(memberId)) {
                missingMemberProxyKeys.add(new Key<ScMemberProxy>(ScMemberProxy.class, memberId));
            }
        }

        if (missingMemberProxyKeys.size() > 0) {
            memberProxies.addAll(ofy().get(missingMemberProxyKeys).values());
        }

        for (ScMemberProxy memberProxy : memberProxies) {
            memberProxy.membershipKeys.addAll(addedMembershipKeys.get(memberProxy.userId));
        }
        
        ofy().put(memberProxies);
        ofy().put(entities);
        
        ScLog.log().fine(m.meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities()
    {
        return fetchEntities(null);
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        ScLog.log().fine(m.meta() + "Fetching entities modified since: " + ((lastFetchDate != null) ? lastFetchDate.toString() : "<dawn of time>"));
        
        Collection<ScMembership> memberships = ofy().get(m.getMemberProxy().membershipKeys).values();
        
        Set<ScCachedEntity> fetchedEntities = new HashSet<ScCachedEntity>();
        Set<Key<ScCachedEntity>> additionalEntityKeys = new HashSet<Key<ScCachedEntity>>();
        
        for (ScMembership membership : memberships) {
            if (membership.isActive) {
                Iterable<ScCachedEntity> membershipEntities = null;
                
                if (lastFetchDate != null) {
                    membershipEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey).filter("dateModified >", lastFetchDate);
                } else {
                    membershipEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey);
                }
                
                for (ScCachedEntity entity : membershipEntities) {
                    if (entity.getClass().equals(ScSharedEntityRef.class)) {
                        additionalEntityKeys.add(((ScSharedEntityRef)entity).sharedEntityKey);
                    }
                    
                    fetchedEntities.add(entity);
                }
            } else {
                additionalEntityKeys.add(new Key<ScCachedEntity>(membership.scolaKey, ScCachedEntity.class, membership.scolaId));
            }
        }
        
        fetchedEntities.addAll(ofy().get(additionalEntityKeys).values());
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        return new ArrayList<ScCachedEntity>(fetchedEntities);
    }
}
