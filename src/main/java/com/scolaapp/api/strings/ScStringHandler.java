package com.scolaapp.api.strings;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("strings")
public class ScStringHandler
{
    @GET
    @Path("{language}")
    @Produces({MediaType.APPLICATION_JSON})
    public ScStrings getStrings(@PathParam("language") String language)
    {
    	return new ScStrings(language);
    }
}
