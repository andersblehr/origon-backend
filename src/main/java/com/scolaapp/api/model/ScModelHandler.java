package com.scolaapp.api.model;

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
    private ScDAO DAO;

    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam("duid")    String deviceId,
                                    @QueryParam("device")  String deviceType,
                                    @QueryParam("version") String appVersion)
    {
        DAO = new ScDAO(deviceId, deviceType, appVersion);
        
        ScLog.log().fine(DAO.meta() + String.format("Data: %s", entities.toString()));
        
        for (ScCachedEntity entity : entities) {
            entity.mapRelationshipKeys();
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
}
