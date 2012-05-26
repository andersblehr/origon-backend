package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scolaapp.api.aux.ScLog;
import com.scolaapp.api.aux.ScMeta;
import com.scolaapp.api.aux.ScURLParams;


@Path("model")
public class ScModelHandler
{
    @POST
    @Path("sync")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response synchroniseEntities(List<ScCachedEntity> entitiesToPersist,
                                        @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date   lastFetchDate,
                                        @QueryParam (ScURLParams.AUTH_TOKEN)        String authToken,
                                        @QueryParam (ScURLParams.APP_VERSION)       String appVersion)
    {
        ScMeta m = new ScMeta(authToken, appVersion);
        
        m.validateLastFetchDate(lastFetchDate);
        
        Date fetchDate = new Date();
        List<ScCachedEntity> fetchedEntities = null;
        
        if (m.isValid()) {
            fetchedEntities = m.getDAO().fetchEntities(lastFetchDate);
            
            if (entitiesToPersist.size() > 0) {
                m.getDAO().persistEntities(entitiesToPersist, fetchDate);
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (entitiesToPersist.size() > 0) {
            return Response.status(HttpServletResponse.SC_CREATED).build();
        } else if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(fetchedEntities).lastModified(fetchDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
        }
    }
    
    
    @GET
    @Path("fetch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchEntities(@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date   lastFetchDate,
                                  @QueryParam (ScURLParams.AUTH_TOKEN)        String authToken,
                                  @QueryParam (ScURLParams.APP_VERSION)       String appVersion)
    {
        ScMeta m = new ScMeta(authToken, appVersion);
        
        m.validateLastFetchDate(lastFetchDate);
        
        Date fetchDate = new Date();
        List<ScCachedEntity> fetchedEntities = null;
        
        if (m.isValid()) {
            fetchedEntities = m.getDAO().fetchEntities(lastFetchDate);
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(fetchedEntities).lastModified(fetchDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
        }
    }
}
