package com.scolaapp.api.utils;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import com.scolaapp.api.ScScolaApplication;


public class ScUtil
{
    private static final Logger log = Logger.getLogger(ScScolaApplication.class.getName());

    private static ScUtil env = null;
    
    
    protected ScUtil() {}
    
    
    public static Logger log()
    {
        return log;
    }

    
    
    public static ScUtil env()
    {
        if (null == env) {
            env = new ScUtil();
        }
        
        return env;
    }
    
    
    public static void throwWebApplicationException(Exception exception, int statusCode, String reason)
    {
        ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
        responseBuilder.status(statusCode);
        
        if (reason != null) {
            responseBuilder.header("reason", reason);
        }
        
        Response response = responseBuilder.build();
        
        if (exception != null) {
            throw new WebApplicationException(exception, response);
        } else {
            throw new WebApplicationException(response);
        }
    }
    
    
    public static void throwWebApplictionException(Exception exception, int statusCode)
    {
        throwWebApplicationException(exception, statusCode, null);
    }
    
    
    public static void throwWebApplictionException(int statusCode, String reason)
    {
        throwWebApplicationException(null, statusCode, reason);
    }
    
    
    public static void throwWebApplicationException(int statusCode)
    {
        throwWebApplicationException(null, statusCode, null);
    }
}
