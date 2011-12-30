package com.scolaapp.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("scola")
public class ScScolaHandler
{
    @GET
    @Path("status")
    @Produces({MediaType.APPLICATION_JSON})
	public String getStatus()
	{
		return "OK";
	}
}
