package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("model")
public class ScModelHandler
{
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam("duid")    String deviceId,
                                    @QueryParam("device")  String deviceType,
                                    @QueryParam("version") String appVersion)
    {
        ScDAO DAO = new ScDAO(deviceId, deviceType, appVersion);
        
        ScLog.log().fine(DAO.meta() + String.format("Data: %s", entities.toString()));
        Date now = new Date();
        
        for (ScCachedEntity entity : entities) {
            entity.mapRelationshipKeys();
            
            if (!entity.isReferenceToSharedEntity()) {
                entity.dateModified = now;
            }
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
    
    
    @GET
    @Path("fetch")
    @Produces({MediaType.APPLICATION_JSON})
    public Response fetchEntities(@QueryParam("duid")      String deviceId,
                                  @QueryParam("device")    String deviceType,
                                  @QueryParam("version")   String appVersion,
                                  @QueryParam("token")     String authToken,
                                  @QueryParam("lastFetch") Date lastFetchDate)
    {
        ScDAO DAO = new ScDAO(deviceId, deviceType, appVersion);
        List<ScCachedEntity> updatedEntities = DAO.fetchEntities(authToken, deviceId, lastFetchDate);
        
        return Response.status(HttpServletResponse.SC_OK).entity(updatedEntities).build();
    }
}
