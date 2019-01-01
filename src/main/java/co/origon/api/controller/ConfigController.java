package co.origon.api.controller;

import co.origon.api.annotation.JwtAuthenticated;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;

import co.origon.api.model.api.entity.Config;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

@Path("configs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@JwtAuthenticated
public class ConfigController {

    @Context
    private HttpServletRequest request;

    @Inject
    private DaoFactory daoFactory;

    @POST
    @Path("{category}")
    public Response createConfig(String configJson, @PathParam("category") String category) {
        try {
            final Dao<Config> configDao = daoFactory.daoFor(Config.class);
            configDao.save(configDao.create()
                    .category(category)
                    .configJson(configJson));
            return Response
                    .created(new URI(request.getContextPath() + request.getPathInfo()))
                    .build();
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException("URI syntax error", e);
        }
    }

    @GET
    @Path("{category}")
    public Response getConfig(@PathParam("category") String category) {
        final String configJson = daoFactory.daoFor(Config.class).get(category).configJson();
        if (configJson == null)
            throw new NotFoundException("No configuration settings for category: " + category);
        return Response.ok(configJson).build();
    }

    @DELETE
    @Path("{category}")
    public Response deleteConfig(@PathParam("category") String category) {
        final Dao<Config> configDao = daoFactory.daoFor(Config.class);
        final Config config = configDao.get(category);
        if (config == null)
            throw new NotFoundException("No configuration settings for category: " + category);
        configDao.delete(configDao.get(category));
        return Response.ok().build();
    }
}
