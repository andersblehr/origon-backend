package com.scolaapp.api.utils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;


public class ScDAO extends DAOBase
{
    static
    {
        ObjectifyService.register(ScAuthInfo.class);
        ObjectifyService.register(ScPerson.class);
        ObjectifyService.register(ScScola.class);
        ObjectifyService.register(ScScolaMember.class);
    }
    
    
    public ScDAO()
    {
        super();
    }
    
    
    public <T> T getOrThrow(Class<T> type, String id)
    {
        T returnable = null;
        
        try {
            returnable = ofy().get(type, id);
        } catch (NotFoundException e) {
            ScUtil.log().severe(String.format("No persisted %s instance with id '%'.", type.getName(), id));
            throw new WebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
