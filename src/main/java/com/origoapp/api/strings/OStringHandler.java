package com.origoapp.api.strings;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.origoapp.api.aux.OLog;
import com.origoapp.api.aux.OMeta;
import com.origoapp.api.aux.OURLParams;


@Path("strings")
public class OStringHandler
{
    @GET
    @Path("{" + OURLParams.STRING_LANGUAGE + "}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStrings(@PathParam (OURLParams.STRING_LANGUAGE) String language,
                               @QueryParam(OURLParams.STRING_TOKEN) String token,
                               @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                               @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                               @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(deviceId, deviceType, appVersion);
        
        if (m.isValid()) {
            if (token != null) {
                if (token.indexOf("=") > 0) {
                    m.validateTimestampToken(token);
                } else {
                    m.validateAuthToken(token);
                }
            } else {
                OLog.log().warning(m.meta() + "Token is missing. Blocking entry for potential intruder, raising BAD_REQUEST (400).");
                OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        
        if (!m.isValid()) {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "Fetched strings.");
        
        return Response.status(HttpServletResponse.SC_OK).entity(new OStrings(language)).build();
    }
}
