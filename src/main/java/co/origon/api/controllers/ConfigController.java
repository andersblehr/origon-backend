package co.origon.api.controllers;

import co.origon.api.filters.JwtAuthenticated;
import co.origon.api.helpers.Config;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("configs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigController {

    private final static String PARAM_CATEGORY = "category";

    @POST
    @Path("{category}")
    public Response createConfig(Config config, @PathParam(PARAM_CATEGORY) String category) {
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("{category}")
    @JwtAuthenticated
    public Response getConfig(@PathParam(PARAM_CATEGORY) String category) {
        return Response.ok("Here it is: " + category).build();
    }
}
