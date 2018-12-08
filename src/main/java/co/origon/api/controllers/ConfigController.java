package co.origon.api.controllers;

//import co.origon.api.filters.JwtAuthenticated;
import co.origon.api.helpers.Config;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Path("configs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
//@JwtAuthenticated
public class ConfigController {

    @Context
    private HttpServletRequest request;

    @POST
    @Path("{category}")
    public Response createConfig(String configJson, @PathParam("category") String category) {
        try {
            final Config config = Config.builder()
                    .category(category)
                    .configJson(new JSONObject(configJson).toString())
                    .build();

            ofy().save().entity(config).now();

            return Response.created(new URI(request.getContextPath() + request.getPathInfo())).build();
        } catch (JSONException e) {
            throw new BadRequestException("Invalid config JSON: " + configJson, e);
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException("URI syntax error. This should never happen", e);
        }
    }

    @GET
    @Path("{category}")
    public Response getConfig(@PathParam("category") String category) {
        try {
            return Response.ok(Config.get(category).toString()).build();
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("No configuration settings for category: " + category, e);
        } catch (JSONException e) {
            throw new InternalServerErrorException("Illegal config JSON for category: " + category, e);
        }
    }
}
