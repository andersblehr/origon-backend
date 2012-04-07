package com.scolaapp.api.aux;

import java.util.ArrayList;
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
import com.scolaapp.api.auth.ScAuthPhase;
import com.scolaapp.api.auth.ScAuthToken;
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
        ObjectifyService.register(ScAuthToken.class);
        
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScMessageBoard.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScMember.class);
        ObjectifyService.register(ScMemberResidency.class);
        ObjectifyService.register(ScMembership.class);
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
    
    
    public void putAuthToken(String authToken, ScAuthPhase authPhase)
    {
        if (authPhase == ScAuthPhase.LOGIN) {
            Query<ScAuthToken> existingTokens = ofy().query(ScAuthToken.class).filter("userId", m.getUserId());
            
            for (ScAuthToken existingToken : existingTokens) {
                if (existingToken.deviceId.equals(m.getDeviceId())) {
                    ofy().delete(existingToken);
                    ScLog.log().fine(m.meta() + String.format("Deleted old auth token (token: %s; user: %s).", existingToken.token, m.getUserId()));
                }
            }
        }
        
        ofy().put(new ScAuthToken(authToken, m.getUserId(), m.getScolaId(), m.getDeviceId(), m.getDeviceType()));
        ScLog.log().fine(m.meta() + String.format("Persisted new auth token (token: %s; user: %s).", authToken, m.getUserId()));
    }
    
    
    public void persistEntities(List<ScCachedEntity> entities)
    {
        ScMemberProxy memberProxy = get(new Key<ScMemberProxy>(ScMemberProxy.class, m.getUserId()));
        
        if (memberProxy != null) {
            memberProxy.externaliseSets();
        } else {
            Iterable<Key<ScAuthToken>> tokenKeyIterable = ofy().query(ScAuthToken.class).filter("userId", m.getUserId()).fetchKeys();
            memberProxy = new ScMemberProxy(m.getUserId(), m.getScolaKey(), tokenKeyIterable);
        }
        
        Set<Key<ScCachedEntity>> sharedEntityKeys = new HashSet<Key<ScCachedEntity>>();
        
        for (ScCachedEntity entity : entities) {
            if (entity.isShared) {
                sharedEntityKeys.add(new Key<ScCachedEntity>(entity.scolaKey, ScCachedEntity.class, entity.entityId));
            }
            
            Class<?> entityClass = entity.getClass();
            
            if (entityClass.equals(ScMemberResidency.class)) {
                memberProxy.residenceKeySet.add(((ScMemberResidency)entity).scolaKey);
            } else if (entityClass.equals(ScMembership.class)) {
                memberProxy.membershipKeySet.add(entity.getKey());
            }
            
            entity.internaliseRelationships();
        }
        
        ofy().put(entities);
        
        ScLog.log().fine(m.meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        ScLog.log().fine(m.meta() + "Fetching entities modified since: " + ((lastFetchDate != null) ? lastFetchDate.toString() : "<dawn of time>"));
        
        Set<ScCachedEntity> modifiedEntities = new HashSet<ScCachedEntity>();
        Set<Key<ScCachedEntity>> externalEntityKeys = new HashSet<Key<ScCachedEntity>>();
        
        Key<ScMember> memberKey = new Key<ScMember>(m.getScolaKey(), ScMember.class, m.getUserId());
        Query<ScMembership> memberships = ofy().query(ScMembership.class).filter("memberKey", memberKey);
        
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
        
        for (ScCachedEntity entity : modifiedEntities) {
            entity.externaliseRelationships();
        }
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + modifiedEntities.toString());
        
        return new ArrayList<ScCachedEntity>(modifiedEntities);
    }
    
    
    public List<ScCachedEntity> fetchEntities()
    {
        return fetchEntities(null);
    }
}
