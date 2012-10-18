package com.origoapp.api.strings;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("strings")
public class OStringHandler
{
    @GET
    @Path("{language}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStrings(@PathParam("language") String language)
    {
        OStrings strings = new OStrings(language);
        
    	return Response.status(HttpServletResponse.SC_OK).entity(strings).build();
    }
}
