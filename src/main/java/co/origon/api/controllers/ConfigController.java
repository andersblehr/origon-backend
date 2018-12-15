package co.origon.api.controllers;

import co.origon.api.common.Config;
import co.origon.api.filters.JwtAuthenticated;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

@Path("configs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JwtAuthenticated
public class ConfigController {

    @Context
    private HttpServletRequest request;

    @POST
    @Path("{category}")
    public Response createConfig(String configJson, @PathParam("category") String category) {
        try {
            Config.create(category, configJson);
            return Response.created(new URI(request.getContextPath() + request.getPathInfo())).build();
        } catch (JSONException e) {
            throw new BadRequestException("Invalid config JSON: " + configJson, e);
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }

    @GET
    @Path("{category}")
    public Response getConfig(@PathParam("category") String category) {
        final JSONObject configJson = doGetConfig(category);
        if (configJson == null) {
            throw new NotFoundException("No configuration settings for category: " + category);
        }

        return Response.ok(configJson.toString()).build();
    }

    @DELETE
    @Path("{category}")
    public Response deleteConfig(@PathParam("category") String category) {
        if (doGetConfig(category) == null) {
            throw new NotFoundException("No configuration settings for category: " + category);
        }

        try {
            Config.delete(category);
            return Response.ok().build();
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }

    private JSONObject doGetConfig(String category) {
        try {
            return Config.get(category);
        } catch (JSONException e) {
            throw new InternalServerErrorException("Illegal config JSON for category: " + category, e);
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred", e);
        }
    }
}
