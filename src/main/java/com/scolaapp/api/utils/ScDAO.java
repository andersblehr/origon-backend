package com.scolaapp.api.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthPhase;
import com.scolaapp.api.auth.ScAuthToken;

import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScDeviceListing;
import com.scolaapp.api.model.ScHousehold;
import com.scolaapp.api.model.ScHouseholdResidency;
import com.scolaapp.api.model.ScMessageBoard;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.model.ScScolaMembership;
import com.scolaapp.api.model.ScSharedEntityRef;




public class ScDAO extends DAOBase
{
    public ScMeta m;
    
    public ScScolaMember scolaMember = null;
    public ScAuthInfo authInfo = null;

    
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        ObjectifyService.register(ScAuthToken.class);
        
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScDeviceListing.class);
        ObjectifyService.register(ScHousehold.class);
        ObjectifyService.register(ScHouseholdResidency.class);
        ObjectifyService.register(ScMessageBoard.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScScolaMember.class);
        ObjectifyService.register(ScScolaMembership.class);
        ObjectifyService.register(ScSharedEntityRef.class);
    }
    
    
    public ScDAO(ScAuthPhase authPhase, ScMeta meta)
    {
        super();
        
        m = meta;
        
        if (authPhase == ScAuthPhase.LOGIN) {
            scolaMember = get(ScScolaMember.class, m.userId);
        } else if (authPhase == ScAuthPhase.CONFIRMATION) {
            authInfo = get(ScAuthInfo.class, m.userId);
        }
        
        if (((authPhase == ScAuthPhase.LOGIN) && (scolaMember == null)) || ((authPhase == ScAuthPhase.CONFIRMATION) && (authInfo == null))) {
            ScLog.log().severe(m.meta() + "Unknown user. Barring entry for potential intruder, raising FORBIDDEN (403).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
    }
    
    
    public ScDAO(String authToken, ScMeta meta)
    {
        super();
        
        m = meta;
        
        try {
            ScAuthToken tokenInfo = ofy().get(ScAuthToken.class, authToken); 

            if (tokenInfo.deviceId.equals(m.deviceId)) {
                Date now = new Date();
                
                if (now.before(tokenInfo.dateExpires)) {
                    m.userId = tokenInfo.userId;
                } else {
                    ScLog.log().severe(m.meta() + String.format("Expired auth token [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken));
                    ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN, "authExpired");
                }
            } else {
                ScLog.log().severe(m.meta() + String.format("Auth token [%s] not valid for device [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken, m.deviceId));
                ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN, "authStolen");
            }
        } catch (NotFoundException e) {
            ScLog.log().severe(m.meta() + String.format("Unknown auth token [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken));
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_FORBIDDEN, "authUnknown");
        }
    }
    
    
    public ScDAO(String authToken, String deviceId, String deviceType, String appVersion)
    {
        this(authToken, new ScMeta(deviceId, deviceType, appVersion));
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
            entity.internaliseRelationshipKeys();
            
            if (!entity.isReferenceToSharedEntity()) {
                entity.dateModified = now;
            }
        }
        
        ofy().put(entities);
        
        ScLog.log().fine(m.meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        List<ScCachedEntity> updatedEntities = new ArrayList<ScCachedEntity>();

        Key<ScScolaMember> memberKey = new Key<ScScolaMember>(ScScolaMember.class, m.userId);
        List<ScScolaMembership> memberships = ofy().query(ScScolaMembership.class).filter("memberKey", memberKey).list();
        
        for (ScScolaMembership membership : memberships) {
            if (membership.isActive) {
                List<ScCachedEntity> updatedEntitiesInScola = null;
                
                if (lastFetchDate != null) {
                    updatedEntitiesInScola = ofy().query(ScCachedEntity.class).filter("scolaKey", membership.scolaKey).filter("dateModified >", lastFetchDate).list();
                } else {
                    updatedEntitiesInScola = ofy().query(ScCachedEntity.class).filter("scolaKey", membership.scolaKey).list();
                }
                
                List<Key<ScCachedEntity>> sharedEntityKeys = new ArrayList<Key<ScCachedEntity>>(); 
                
                for (ScCachedEntity entity : updatedEntitiesInScola) {
                    if (entity.isReferenceToSharedEntity()) {
                        ScSharedEntityRef entityRef = (ScSharedEntityRef)entity;
                        sharedEntityKeys.add(entityRef.sharedEntityKey);
                    } else {
                        updatedEntities.add(entity.getClass().cast(entity));
                    }
                }
                
                if (sharedEntityKeys.size() > 0) {
                    Map<Key<ScCachedEntity>, ScCachedEntity> sharedEntitiesMap = ofy().get(sharedEntityKeys);
                    
                    for (Map.Entry<Key<ScCachedEntity>, ScCachedEntity> sharedEntityEntry : sharedEntitiesMap.entrySet()) {
                        updatedEntities.add(sharedEntityEntry.getValue());
                    }
                }
            }
        }
        
        for (ScCachedEntity entity : updatedEntities) {
            entity.externaliseRelationshipKeys();
        }
        
        ScLog.log().fine(m.meta() + "Fetched entities: " + updatedEntities.toString());
        
        return updatedEntities;
    }
}
