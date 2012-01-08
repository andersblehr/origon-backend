package com.scolaapp.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.scolaapp.api.utils.ScLog;


@Path("scola")
public class ScScolaHandler
{
    @GET
    @Path("status")
    @Produces({MediaType.APPLICATION_JSON})
	public String getStatus()
	{
        ScLog.log().info("Checking server availability...");
		return "OK";
	}
}
