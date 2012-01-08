package com.scolaapp.api.utils;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import com.scolaapp.api.ScScolaApplication;


public class ScLog
{
    private static final Logger log = Logger.getLogger(ScScolaApplication.class.getName());

    private static final int SEVERE = 0;
    private static final int WARNING = 1;
    private static final int INFO = 2;
    private static final int FINE = 3;
    private static final int FINER = 4;
    private static final int FINEST = 5;
    
    
    protected ScLog() {}
    
    
    protected static void log(ScAppEnv env, String message, int level)
    {
        String messageWithMetadata = String.format("[Version %s][%s][%s] %s", env.appVersion, env.deviceType, env.deviceUUID, message);
        
        switch (level) {
            case SEVERE:
                log.severe(messageWithMetadata);
                break;
            case WARNING:
                log.warning(messageWithMetadata);
                break;
            case INFO:
                log.info(messageWithMetadata);
                break;
            case FINE:
                log.fine(messageWithMetadata);
                break;
            case FINER:
                log.finer(messageWithMetadata);
                break;
            default:
                break;
        }
    }
    
    
    public static Logger log()
    {
        return log;
    }

    
    public static void severe(ScAppEnv env, String message)
    {
        log(env, message, SEVERE);
    }
    
    
    public static void warning(ScAppEnv env, String message)
    {
        log(env, message, WARNING);
    }
    
    
    public static void info(ScAppEnv env, String message)
    {
        log(env, message, INFO);
    }
    
    
    public static void fine(ScAppEnv env, String message)
    {
        log(env, message, FINE);
    }
    
    
    public static void finer(ScAppEnv env, String message)
    {
        log(env, message, FINER);
    }
    
    
    public static void finest(ScAppEnv env, String message)
    {
        log(env, message, FINEST);
    }
    
    
    public static void throwWebApplicationException(Exception exception, int statusCode, String reason)
    {
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.status(statusCode);
        
        if (reason != "") {
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
}
