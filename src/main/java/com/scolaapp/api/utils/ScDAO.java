package com.scolaapp.api.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
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
    public ScScolaMember scolaMember = null;
    public ScAuthInfo authInfo = null;
    
    private static final HashMap<String, ScSessionMetadata> sessionMetadataMap = new HashMap<String, ScSessionMetadata>();
    
    private String deviceId;
    private String userId;
    
    
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
    
    
    public ScDAO(String userId, String deviceId, String deviceType, String appVersion, ScAuthPhase authPhase)
    {
        super();
        
        sessionMetadataMap.put(deviceId, new ScSessionMetadata(deviceType, appVersion));
        
        if ((userId != null) && (deviceId != null) && (deviceType != null) && (appVersion != null)) {
            this.userId = userId;
            this.deviceId = deviceId;
            
            if (authPhase == ScAuthPhase.LOGIN) {
                scolaMember = get(ScScolaMember.class, userId);
            } else if (authPhase == ScAuthPhase.CONFIRMATION) {
                authInfo = get(ScAuthInfo.class, userId);
            }
            
            if (((authPhase == ScAuthPhase.LOGIN) && (scolaMember == null)) || ((authPhase == ScAuthPhase.CONFIRMATION) && (authInfo == null))) {
                ScLog.log().severe(this.meta() + "Unknown user. Barring entry for potential intruder, raising FORBIDDEN (403).");
                ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            ScLog.log().severe(this.meta() + String.format("Incomplete request [userId = %s, deviceId = %s, deviceType = %s, appVersion = %s], raising BAD_REQUEST (400).", userId, deviceId, deviceType, appVersion));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    
    public ScDAO(String authToken, String deviceId, String deviceType, String appVersion)
    {
        super();
        
        sessionMetadataMap.put(deviceId, new ScSessionMetadata(deviceType, appVersion));
        
        if ((authToken != null) && (deviceId != null) && (deviceType != null) && (appVersion != null)) {
            this.deviceId = deviceId;

            try {
                ScAuthToken tokenInfo = ofy().get(ScAuthToken.class, authToken); 

                if (tokenInfo.deviceId.equals(deviceId)) {
                    Date now = new Date();
                    
                    if (now.before(tokenInfo.dateExpires)) {
                        userId = tokenInfo.userId;
                    } else {
                        ScLog.log().severe(this.meta() + String.format("Expired auth token [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken));
                        ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN, "authExpired");
                    }
                } else {
                    ScLog.log().severe(this.meta() + String.format("Auth token [%s] not valid for device [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken, deviceId));
                    ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN, "authStolen");
                }
            } catch (NotFoundException e) {
                ScLog.log().severe(this.meta() + String.format("Unknown auth token [%s]. Barring entry for potential intruder, raising FORBIDDEN (403).", authToken));
                ScLog.throwWebApplicationException(e, HttpServletResponse.SC_FORBIDDEN, "authUnknown");
            }
        } else {
            ScLog.log().severe(this.meta() + String.format("Incomplete request [authToken = %s, deviceId = %s, deviceType = %s, appVersion = %s], raising BAD_REQUEST (400).", authToken, deviceId, deviceType, appVersion));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
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
        ScLog.log().fine(this.meta() + String.format("Found %d auth tokens for this device.", tokensForThisDevice.size()));
        
        for (ScAuthToken token : tokensForThisDevice) {
            if (token.userId.equals(userId)) {
                ScLog.log().fine(this.meta() + String.format("Deleting old auth token [%s].", token.authToken));
                ofy().delete(token);
            }
        }
        
        ScLog.log().fine(this.meta() + String.format("Persisting new auth token [%s].", authToken));
        ofy().put(new ScAuthToken(authToken, deviceId, userId));
        
        this.userId = userId;
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
        
        ScLog.log().fine(meta() + "Persisted entities: " + entities.toString());
    }
    
    
    public List<ScCachedEntity> fetchEntities(Date lastFetchDate)
    {
        List<ScCachedEntity> updatedEntities = new ArrayList<ScCachedEntity>();

        Key<ScScolaMember> memberKey = new Key<ScScolaMember>(ScScolaMember.class, userId);
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
        
        ScLog.log().fine(meta() + "Fetched entities: " + updatedEntities.toString());
        
        return updatedEntities;
    }

    
    public String meta()
    {
        ScSessionMetadata sessionMetadata = sessionMetadataMap.get(deviceId);
        
        if (sessionMetadata != null) {
            return String.format("[%s] %s/%s: ", deviceId.substring(0, 8), sessionMetadata.deviceType, sessionMetadata.appVersion);
        } else {
            return String.format("[%s] <unregistered>: ", deviceId.substring(0, 8));
        }
    }

    
    private class ScSessionMetadata
    {
        private String deviceType;
        private String appVersion;
        
        
        public ScSessionMetadata(String type, String version)
        {
            deviceType = type;
            appVersion = version;
        }
    }
}
