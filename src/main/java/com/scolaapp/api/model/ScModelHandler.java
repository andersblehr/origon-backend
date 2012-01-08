package com.scolaapp.api.model;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;

import com.googlecode.objectify.*;

import com.scolaapp.api.utils.ScDAO;


@Path("model")
public class ScModelHandler
{
    private static final ScDAO DAO = new ScDAO(null);
    
    
    @GET
    @Path("person")
    @Produces({MediaType.APPLICATION_JSON})
    public ScPerson getPerson(@DefaultValue("none") @QueryParam("email") String email)
    {
    	ScPerson person = null;
    	
        try {
            person = DAO.ofy().get(ScPerson.class, email);
        } catch (NotFoundException e) {
            throw new WebApplicationException(e, HttpServletResponse.SC_NOT_FOUND);
        }
        
        return person;
    }
    
    
    @POST
    @Path("person")
    public void addPerson(@Form ScPerson person)
    {
        
    }
}
