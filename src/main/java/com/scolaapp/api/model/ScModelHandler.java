package com.scolaapp.api.model;

import java.util.ArrayList;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;

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
    @Path("member")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addMember(@Form ScScolaMember member)
    {
        ScLog.log().fine(String.format("member: %s", member.toString()));
    }
    
    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public void persistEntities(@Form ArrayList<ScCachedEntity> dataArray)
    {
        ScLog.log().fine(String.format("Data: %s", dataArray.toString()));
    }
}
