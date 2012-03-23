package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scolaapp.api.utils.ScDAO;


@Path("model")
public class ScModelHandler
{
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam("duid")    String deviceId,
                                    @QueryParam("device")  String deviceType,
                                    @QueryParam("version") String appVersion,
                                    @QueryParam("token")   String authToken)
    {
        ScDAO DAO = new ScDAO(deviceId, deviceType, appVersion);
        
        if (DAO.lookUpUserId(authToken, deviceId) != null) {
            DAO.persistEntities(entities);
        }
        
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
        
        List<ScCachedEntity> updatedEntities = null;
        String userId = DAO.lookUpUserId(authToken, deviceId);
        
        if (userId != null) {
            updatedEntities = DAO.fetchEntities(userId, lastFetchDate);
        }
        
        return Response.status(HttpServletResponse.SC_OK).entity(updatedEntities).build();
    }
}
