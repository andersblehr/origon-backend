package com.scolaapp.api.aux;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthToken;
import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScDevice;
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
    
    
    public <T> T get(Class<T> clazz, String id)
    {
        try {
            return ofy().get(clazz, id);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    
    public void putAuthToken(String authToken, String userId, String deviceId)
    {
        List<ScAuthToken> tokensForThisDevice = ofy().query(ScAuthToken.class).filter("deviceId", deviceId).list();
        ScLog.log().fine(m.meta() + String.format("Found %d auth tokens for this device.", tokensForThisDevice.size()));
        
        for (ScAuthToken token : tokensForThisDevice) {
            if (token.userId.equals(userId)) {
                ScLog.log().fine(m.meta() + String.format("Deleting old auth token [%s].", token.authToken));
                ofy().delete(token);
            }
        }
        
        ScLog.log().fine(m.meta() + String.format("Persisting new auth token [%s].", authToken));
        ofy().put(new ScAuthToken(authToken, userId, deviceId));
    }
    
    
    public void persistEntities(List<ScCachedEntity> entities)
    {
        Date now = new Date();
        
        for (ScCachedEntity entity : entities) {
            entity.dateModified = now;
            entity.internaliseRelationships();
        }
        
        ofy().put(entities);
        
        ScLog.log().fine(m.meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        ScLog.log().fine(m.meta() + "Fetching entities modified since: " + ((lastFetchDate != null) ? lastFetchDate.toString() : "<dawn of time>"));
        
        Set<ScCachedEntity> updatedEntities = new HashSet<ScCachedEntity>();
        
        Set<ScCachedEntity> scolaEntities = new HashSet<ScCachedEntity>();
        Set<Key<ScCachedEntity>> additionalEntityKeys = new HashSet<Key<ScCachedEntity>>();
        
        Key<ScMember> userMemberKey = new Key<ScMember>(ScMember.class, m.getUserId());
        List<ScMembership> userMemberships = ofy().query(ScMembership.class).filter("memberKey", userMemberKey).list();
        
        for (ScMembership userMembership : userMemberships) {
            if (userMembership.isActive) {
                if (lastFetchDate != null) {
                    scolaEntities.addAll(ofy().query(ScCachedEntity.class).filter("scolaKey", userMembership.scolaKey).filter("dateModified >", lastFetchDate).list());
                } else {
                    scolaEntities.addAll(ofy().query(ScCachedEntity.class).filter("scolaKey", userMembership.scolaKey).list());
                }
                
                for (ScCachedEntity entity : scolaEntities) {
                    if (entity.getClass().equals(ScSharedEntityRef.class)) {
                        additionalEntityKeys.add(new Key<ScCachedEntity>(ScCachedEntity.class, ((ScSharedEntityRef)entity).entityRefId));
                    }
                    
                    updatedEntities.add(entity);
                }
            } else {
                additionalEntityKeys.add(new Key<ScCachedEntity>(ScCachedEntity.class, userMembership.scolaKey.getRaw().getName()));
            }
        }
        
        if (additionalEntityKeys.size() > 0) {
            updatedEntities.addAll(ofy().get(additionalEntityKeys).values());
        }
        
        for (ScCachedEntity entity : updatedEntities) {
            entity.externaliseRelationships();
        }
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + updatedEntities.toString());
        
        return new ArrayList<ScCachedEntity>(updatedEntities);
    }
    
    
    public List<ScCachedEntity> fetchEntities()
    {
        return fetchEntities(null);
    }
}
