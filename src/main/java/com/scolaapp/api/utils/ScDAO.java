package com.scolaapp.api.utils;

import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;


public class ScDAO extends DAOBase
{
    private ScAppEnv env;
    
    
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        ObjectifyService.register(ScDevice.class);
        ObjectifyService.register(ScPerson.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScScolaMember.class);
    }
    
    
    public ScDAO(ScAppEnv env_)
    {
        super();
        
        env = env_;
    }
    
    
    public <T> T getOrThrow(Class<T> type, String id)
    {
        T returnable = null;
        
        try {
            returnable = ofy().get(type, id);
        } catch (NotFoundException e) {
            ScLog.warning(env, String.format("No persisted %s instance with id '%s'.", type.getName(), id));
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_NOT_FOUND, type);
        }
        
        return returnable;
    }
    
    
    public <T> T get(Class<T> type, String id)
    {
        try {
            return ofy().get(type, id);
        } catch (NotFoundException e) {
            return null;
        }
    }
}