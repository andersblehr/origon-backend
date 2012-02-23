package com.scolaapp.api.utils;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import com.scolaapp.api.ScScolaApplication;


public class ScLog
{
    private static final Logger log = Logger.getLogger(ScScolaApplication.class.getName());
    private static final HashMap<String, ScLogMeta> metaMap = new HashMap<String, ScLogMeta>();

    
    protected ScLog() {}
    
    
    public static Logger log()
    {
        return log;
    }
    
    
    public static void setMeta(String deviceUUID, String deviceType, String appVersion)
    {
        ScLogMeta meta = metaMap.get(deviceUUID);
        
        if (meta == null) {
            meta = new ScLogMeta(deviceUUID, deviceType, appVersion);
            metaMap.put(deviceUUID, meta);
        }
    }
    
    
    public static String meta(String deviceUUID)
    {
        return metaMap.get(deviceUUID).meta();
    }

    
    public static void throwWebApplicationException(Exception exception, int statusCode, String reason)
    {
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.status(statusCode);
        
        if ((reason != null) && (reason != "")) {
            responseBuilder.header("reason", reason);
        }
        
        Response response = responseBuilder.build();
        
        if (exception != null) {
            throw new WebApplicationException(exception, response);
        } else {
            throw new WebApplicationException(response);
        }
    }
    
    
    public static void throwWebApplicationException(Exception exception, int statusCode, Class<?> clazz)
    {
        String[] pathElements = clazz.getName().split("\\.");
        String className = pathElements[pathElements.length - 1];
        
        throwWebApplicationException(exception, statusCode, className);
    }
    
    
    public static void throwWebApplicationException(int statusCode, Class<?> clazz)
    {
        throwWebApplicationException(null, statusCode, clazz);
    }
    
    
    public static void throwWebApplicationException(Exception exception, int statusCode)
    {
        throwWebApplicationException(exception, statusCode, "");
    }
    
    
    public static void throwWebApplicationException(int statusCode, String reason)
    {
        throwWebApplicationException(null, statusCode, reason);
    }
    
    
    public static void throwWebApplicationException(int statusCode)
    {
        throwWebApplicationException(null, statusCode, "");
    }

    
    private static class ScLogMeta
    {
        private String appVersion;
        private String deviceType;
        private String deviceUUID;
        
        
        public ScLogMeta(String UUID, String type, String version)
        {
            deviceUUID = UUID;
            deviceType = type;
            appVersion = version;
        }
        
        
        public String meta()
        {
            return String.format("%s/%s [%s]", deviceType, appVersion, deviceUUID.substring(0, 8));
        }
    }
}
