package com.scolaapp.api.utils;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;

import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScDeviceListing;
import com.scolaapp.api.model.ScHousehold;
import com.scolaapp.api.model.ScHouseholdResidency;
import com.scolaapp.api.model.ScMessageBoard;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.model.ScScolaMembership;




public class ScDAO extends DAOBase
{
    private static final HashMap<String, ScSessionMetadata> sessionMetadataMap = new HashMap<String, ScSessionMetadata>();
    
    private String deviceId;
    
    
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScDeviceListing.class);
        ObjectifyService.register(ScHousehold.class);
        ObjectifyService.register(ScHouseholdResidency.class);
        ObjectifyService.register(ScMessageBoard.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScScolaMember.class);
        ObjectifyService.register(ScScolaMembership.class);
    }
    
    
    public ScDAO(String deviceId, String deviceType, String appVersion, boolean deviceMustExist)
    {
        super();
        
        this.deviceId = deviceId;
        
        if (deviceMustExist) {
            ScDevice device = get(ScDevice.class, deviceId);
            
            if (device == null) {
                ScAuthInfo authInfo = get(ScAuthInfo.class, deviceId);
                
                if (authInfo != null) {
                    ofy().delete(authInfo);
                } else {
                    ScLog.log().severe(this.meta() + "Unknown device. Barring entry for potential intruder, raising UNAUTHORIZED (401).");
                    ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
        
        sessionMetadataMap.put(deviceId, new ScSessionMetadata(deviceType, appVersion));
    }
    
    
    public ScDAO(String deviceId, String deviceType, String appVersion)
    {
        this(deviceId, deviceType, appVersion, true);
    }
    
    
    public <T> T getOrThrow(Class<T> clazz, String id)
    {
        T returnable = null;
        
        try {
            returnable = ofy().get(clazz, id);
        } catch (NotFoundException e) {
            ScLog.log().warning(this.meta() + String.format("No persisted %s instance with id '%s', raising NOT_FOUND (404).", clazz.getName(), id));
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_NOT_FOUND, clazz);
        }
        
        return returnable;
    }
    
    
    public <T> T get(Class<T> clazz, String id)
    {
        try {
            return ofy().get(clazz, id);
        } catch (NotFoundException e) {
            return null;
        }
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
