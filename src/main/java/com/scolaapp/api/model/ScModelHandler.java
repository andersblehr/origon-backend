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
    @Path("persist")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam (ScURLParams.AUTH_TOKEN)  String authToken,
                                    @QueryParam (ScURLParams.DEVICE_ID)   String deviceId,
                                    @QueryParam (ScURLParams.DEVICE_TYPE) String deviceType,
                                    @QueryParam (ScURLParams.APP_VERSION) String appVersion)
    {
        ScMeta m = new ScMeta(deviceId, deviceType, appVersion);
        
        m.validateAuthToken(authToken);
        
        if (m.isValid()) {
            m.getDAO().persistEntities(entities);
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising FORBIDDEN (403).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
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
        ScMeta m = new ScMeta(deviceId, deviceType, appVersion);
        
        Date now = new Date();
        List<ScCachedEntity> updatedEntities = null;
        
        m.validateAuthToken(authToken);
        
        if (m.isValid()) {
            updatedEntities = m.getDAO().fetchEntities(lastFetchDate);
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising FORBIDDEN (403).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        if (updatedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(updatedEntities).lastModified(now).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).build();
        }
    }
}
