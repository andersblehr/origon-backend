package com.scolaapp.api.model;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScURLParams;


@Path("model")
public class ScModelHandler
{
    @POST
    @Path("persist")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam (ScURLParams.AUTH_TOKEN)  String authToken,
                                    @QueryParam (ScURLParams.DEVICE_ID)   String deviceId,
                                    @QueryParam (ScURLParams.DEVICE_TYPE) String deviceType,
                                    @QueryParam (ScURLParams.APP_VERSION) String appVersion)
    {
        ScDAO DAO = new ScDAO(authToken, deviceId, deviceType, appVersion);
        
        DAO.persistEntities(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
    
    
    @GET
    @Path("fetch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchEntities(@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date   lastFetchDate,
                                  @QueryParam (ScURLParams.AUTH_TOKEN)        String authToken,
                                  @QueryParam (ScURLParams.DEVICE_ID)         String deviceId,
                                  @QueryParam (ScURLParams.DEVICE_TYPE)       String deviceType,
                                  @QueryParam (ScURLParams.APP_VERSION)       String appVersion)
    {
        ScDAO DAO = new ScDAO(authToken, deviceId, deviceType, appVersion);
        
        List<ScCachedEntity> updatedEntities = null;
        Date now = new Date();
        
        updatedEntities = DAO.fetchEntities(lastFetchDate);
        
        if (updatedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(updatedEntities).lastModified(now).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
        }
    }
}
