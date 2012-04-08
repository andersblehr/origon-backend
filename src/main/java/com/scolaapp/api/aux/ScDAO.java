package com.scolaapp.api.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthTokenMeta;
import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScMemberProxy;
import com.scolaapp.api.model.ScMemberResidency;
import com.scolaapp.api.model.ScMessageBoard;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScMember;
import com.scolaapp.api.model.ScMembership;
import com.scolaapp.api.model.ScSharedEntityRef;


public class ScDAO extends DAOBase
{
    public ScMeta m;

    
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        ObjectifyService.register(ScAuthTokenMeta.class);
        
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScMessageBoard.class);
        ObjectifyService.register(ScMember.class);
        ObjectifyService.register(ScMemberProxy.class);
        ObjectifyService.register(ScMemberResidency.class);
        ObjectifyService.register(ScMembership.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScSharedEntityRef.class);
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
        ScAuthTokenMeta newAuthTokenMeta = new ScAuthTokenMeta(authToken, m.getUserId(), m.getScolaId(), m.getDeviceId(), m.getDeviceType());
        
        ScMemberProxy memberProxy = m.getMemberProxy();
        Collection<ScAuthTokenMeta> authTokenMetaItems = ofy().get(memberProxy.authMetaKeySet).values();
        
        for (ScAuthTokenMeta authTokenMeta : authTokenMetaItems) {
            if (authTokenMeta.deviceId.equals(m.getDeviceId())) {
                memberProxy.authMetaKeySet.remove(new Key<ScAuthTokenMeta>(ScAuthTokenMeta.class, authTokenMeta.authToken));
                ofy().delete(authTokenMeta);
                
                ScLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", authTokenMeta.authToken, m.getUserId()));
            }
        }
        
        memberProxy.authMetaKeySet.add(new Key<ScAuthTokenMeta>(ScAuthTokenMeta.class, newAuthTokenMeta.authToken));
        
        ofy().put(memberProxy, newAuthTokenMeta);
        
        ScLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getUserId()));
    }
    
    
    public void persistEntities(List<ScCachedEntity> entities)
    {
        ScMemberProxy memberProxy = m.getMemberProxy();
        
        Set<Key<ScCachedEntity>> sharedEntityKeys = new HashSet<Key<ScCachedEntity>>();
        boolean memberProxyDidChange = false;
        
        for (ScCachedEntity entity : entities) {
            if (entity.isShared) {  // TODO: This if block is only half-baked
                sharedEntityKeys.add(new Key<ScCachedEntity>(entity.scolaKey, ScCachedEntity.class, entity.entityId));
            }
            
            boolean residenceKeySetDidChange = false;
            boolean membershipKeySetDidChange = false;
            
            if (entity.scolaId.equals(m.getScolaId())) {
                if (entity.getClass().equals(ScMembership.class) || entity.getClass().equals(ScMemberResidency.class)) {
                    Key<ScScola> scolaKey = new Key<ScScola>(ScScola.class, entity.scolaId);
                    Key<ScMembership> membershipKey = new Key<ScMembership>(scolaKey, ScMembership.class, entity.entityId);
                    
                    membershipKeySetDidChange = memberProxy.membershipKeySet.add(membershipKey);
                }
            }
            
            if (!memberProxyDidChange) {
                memberProxyDidChange = residenceKeySetDidChange || membershipKeySetDidChange;
            }
            
            entity.internaliseRelationships();
        }
        
        if (memberProxyDidChange) {
            ofy().put(memberProxy);
        }
        
        ofy().put(entities);
        
        ScLog.log().fine(m.meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        ScLog.log().fine(m.meta() + "Fetching entities modified since: " + ((lastFetchDate != null) ? lastFetchDate.toString() : "<dawn of time>"));
        
        Collection<ScMembership> memberships = ofy().get(m.getMemberProxy().membershipKeySet).values();
        
        Set<ScCachedEntity> modifiedEntities = new HashSet<ScCachedEntity>();
        Set<Key<ScCachedEntity>> externalEntityKeys = new HashSet<Key<ScCachedEntity>>();
        
        for (ScMembership membership : memberships) {
            if (membership.isActive) {
                Query<ScCachedEntity> scolaEntities = null;
                
                if (lastFetchDate != null) {
                    scolaEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey).filter("dateModified >", lastFetchDate);
                } else {
                    scolaEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey);
                }
                
                for (ScCachedEntity entity : scolaEntities) {
                    if (entity.isSharedEntityRef()) {
                        ScSharedEntityRef sharedEntityRef = (ScSharedEntityRef)entity;
                        
                        externalEntityKeys.add(new Key<ScCachedEntity>(sharedEntityRef.scolaKey, ScCachedEntity.class, sharedEntityRef.entityId));
                    }
                    
                    modifiedEntities.add(entity);
                }
            } else {
                externalEntityKeys.add(new Key<ScCachedEntity>(membership.scolaKey, ScCachedEntity.class, membership.scolaKey.getRaw().getName()));
            }
        }
        
        if (externalEntityKeys.size() > 0) {
            modifiedEntities.addAll(ofy().get(externalEntityKeys).values());
        }
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + modifiedEntities.toString());
        
        return new ArrayList<ScCachedEntity>(modifiedEntities);
    }
    
    
    public List<ScCachedEntity> fetchEntities()
    {
        return fetchEntities(null);
    }
}
