package com.scolaapp.api.model;

import java.util.List;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.googlecode.objectify.*;

import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("model")
public class ScModelHandler
{
    private static final ScDAO DAO = new ScDAO(null);
    
    
    @GET
    @Path("member")
    @Produces({MediaType.APPLICATION_JSON})
    public ScScolaMember getMember(@DefaultValue("none") @QueryParam("email") String email)
    {
    	ScScolaMember member = null;
    	
        try {
            member = DAO.ofy().get(ScScolaMember.class, email);
        } catch (NotFoundException e) {
            throw new WebApplicationException(e, HttpServletResponse.SC_NOT_FOUND);
        }
        
        return member;
    }
    
    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public void persistEntities(List<ScCachedEntity> objects)
    {
        ScLog.log().fine(String.format("Data: %s", objects.toString()));
        
        ScScolaMember member = (ScScolaMember)objects.get(0);
        
        ScLog.log().fine(String.format("name: %s", member.name));
        ScLog.log().fine(String.format("email: %s", member.email));
        ScLog.log().fine(String.format("date of birth: %s", member.dateOfBirth.toString()));
        ScLog.log().fine(String.format("gender: %s", member.gender));
        ScLog.log().fine(String.format("address: %s, %s", member.household.addressLine1, member.household.postCodeAndCity));
    }
}
