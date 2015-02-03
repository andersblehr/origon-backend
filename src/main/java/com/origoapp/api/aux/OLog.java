package com.origoapp.api.aux;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.origoapp.api.OOrigoApplication;


public class OLog
{
    private static final Logger log = Logger.getLogger(OOrigoApplication.class.getName());

    
    protected OLog() {}
    
    
    public static Logger log()
    {
        return log;
    }
    
    
    public static void throwWebApplicationException(Exception exception, int statusCode, String reason)
    {
        Response.ResponseBuilder responseBuilder = Response.status(statusCode);
        
        if (reason != null && reason != "") {
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
        throwWebApplicationException(exception, statusCode, clazz.getSimpleName());
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
