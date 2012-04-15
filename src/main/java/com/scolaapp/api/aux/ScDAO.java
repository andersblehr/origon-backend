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
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthTokenMeta;
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
        ObjectifyService.register(ScAuthTokenMeta.class);
        
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
        ScAuthTokenMeta newAuthTokenMeta = new ScAuthTokenMeta(authToken, m.getUserId(), m.getScolaId(), m.getDeviceId());
        
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
    
    
    public void persistEntities(List<ScCachedEntity> entityList)
    {
        ScMemberProxy memberProxy = m.getMemberProxy();
        
        Set<ScCachedEntity> entities = new HashSet<ScCachedEntity>(entityList);
        
        boolean memberProxyDidChange = false;
        boolean keySetDidChange = false;
        
        Date now = new Date();
        
        for (ScCachedEntity entity : entities) {
            entity.scolaKey = new Key<ScScola>(ScScola.class, entity.scolaId);
            entity.dateModified = now;
            
            if (entity.isMembershipForUser(memberProxy.userId)) {
                keySetDidChange = memberProxy.membershipKeySet.add(new Key<ScMembership>(entity.scolaKey, ScMembership.class, entity.entityId));
                
                if (!memberProxyDidChange) {
                    memberProxyDidChange = keySetDidChange;
                }
            }
        }
        
        if (memberProxyDidChange) {
            ofy().put(memberProxy);
        }
        
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
        
        Collection<ScMembership> memberships = ofy().get(m.getMemberProxy().membershipKeySet).values();
        
        Set<ScCachedEntity> memberEntities = new HashSet<ScCachedEntity>();
        Set<Key<ScCachedEntity>> sharedEntityKeys = new HashSet<Key<ScCachedEntity>>();
        Set<Key<ScScola>> pendingScolaKeys = new HashSet<Key<ScScola>>(); 
        
        for (ScMembership membership : memberships) {
            if (membership.isActive) {
                Iterable<ScCachedEntity> scolaEntities = null;
                
                if (lastFetchDate != null) {
                    scolaEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey).filter("dateModified >", lastFetchDate);
                } else {
                    scolaEntities = ofy().query(ScCachedEntity.class).ancestor(membership.scolaKey);
                }
                
                for (ScCachedEntity entity : scolaEntities) {
                    if (entity.isSharedEntityRef()) {
                        sharedEntityKeys.add(((ScSharedEntityRef)entity).sharedEntityKey);
                    }
                    
                    memberEntities.add(entity);
                }
            } else {
                pendingScolaKeys.add(membership.scolaKey); // TODO: Much to do here, and above and below..
            }
        }
        
        memberEntities.addAll(ofy().get(sharedEntityKeys).values());
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + memberEntities.toString());
        
        return new ArrayList<ScCachedEntity>(memberEntities);
    }
}
