package co.origon.api.auth;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import co.origon.api.aux.OLog;
import co.origon.api.aux.OMailer;
import co.origon.api.aux.OMemberProxy;
import co.origon.api.aux.OMeta;
import co.origon.api.aux.OURLParams;
import co.origon.api.model.OReplicatedEntity;
import static co.origon.api.aux.OObjectifyService.ofy;


@Path("auth")
public class OAuthHandler
{
    private OMeta m;
    
    
    @GET
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date deviceReplicationDate,
                                 @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                 @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                                 @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                                 @QueryParam(OURLParams.APP_VERSION) String appVersion,
                                 @QueryParam(OURLParams.LANGUAGE) String language)
    {
        m = new OMeta(deviceId, deviceType, appVersion, language);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.REGISTER);
        m.validateAuthToken(authToken);
        
        OAuthInfo authInfo = null;
        
        if (m.isValid()) {
            authInfo = m.getAuthInfo();
            ofy().save().entity(authInfo).now();
            
            new OMailer(m).sendRegistrationEmail();
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "HTTP status: 201");
        return Response.status(HttpServletResponse.SC_CREATED).entity(authInfo).build();
    }    
    
    
    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date deviceReplicationDate,
                              @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                              @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                              @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                              @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        m = new OMeta(deviceId, deviceType, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.LOGIN);
        m.validateAuthToken(authToken);
        
        OMemberProxy memberProxy = null;
        List<OReplicatedEntity> fetchedEntities = null;
        Date replicationDate = new Date();
        
        if (m.isValid()) {
            memberProxy = m.getMemberProxy();
            
            if (memberProxy != null && memberProxy.didRegister) {
                if (memberProxy.passwordHash.equals(m.getPasswordHash())) {
                    m.getDAO().putAuthToken(authToken);
                    
                    fetchedEntities = m.getDAO().fetchEntities(deviceReplicationDate);
                } else {
                    OLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                    OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                OLog.log().warning(m.meta() + "User is inactive or does not exist, raising UNAUTHORIZED (401).");
                OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        if (fetchedEntities.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).header(HttpHeaders.LOCATION, memberProxy.memberId).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 304");
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                 @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                                 @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                                 @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        m = new OMeta(deviceId, deviceType, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.ACTIVATE);
        m.validateAuthToken(authToken);
        
        List<OReplicatedEntity> fetchedEntities = null;
        Date replicationDate = new Date();
        
        if (m.isValid()) {
            OAuthInfo authInfo = m.getAuthInfo();
            
            if (authInfo.passwordHash.equals(m.getPasswordHash())) {
                ofy().delete().entity(authInfo);
                m.getDAO().putAuthToken(authToken);
                
                fetchedEntities = m.getDAO().fetchEntities(null);
            } else {
                OLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising UNAUTHORIZED (401).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        if (fetchedEntities.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).header(HttpHeaders.LOCATION, m.getMemberProxy().memberId).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 204");
            return Response.status(HttpServletResponse.SC_NO_CONTENT).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("change")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                   @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                   @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        m = new OMeta(authToken, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.CHANGE);
        
        if (m.isValid()) {
            updatePasswordHash(authorizationHeader);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "Saved updated password hash");
        OLog.log().fine(m.meta() + "HTTP status: 201");
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
    
    
    @GET
    @Path("reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                  @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                  @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                                  @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                                  @QueryParam(OURLParams.APP_VERSION) String appVersion,
                                  @QueryParam(OURLParams.LANGUAGE) String language)
    {
        m = new OMeta(deviceId, deviceType, appVersion, language);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.RESET);
        m.validateAuthToken(authToken);
        
        if (m.isValid()) {
            updatePasswordHash(authorizationHeader);
            
            new OMailer(m).sendPasswordResetEmail();
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "Saved temporary password hash");
        OLog.log().fine(m.meta() + "HTTP status: 201");
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
    
    
    @GET
    @Path("sendcode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendActivationCode(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                       @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                       @QueryParam(OURLParams.APP_VERSION) String appVersion,
                                       @QueryParam(OURLParams.LANGUAGE) String language)
    {
        m = new OMeta(authToken, appVersion, language);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.SENDCODE);
        
        OAuthInfo authInfo = null;
        
        if (m.isValid()) {
            authInfo = m.getAuthInfo();
            
            new OMailer(m).sendEmailActivationCode();
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        OLog.log().fine(m.meta() + "HTTP status: 201");
        return Response.status(HttpServletResponse.SC_CREATED).entity(authInfo).build();
    }


    private void updatePasswordHash(String authorizationHeader)
    {
        if (m.isValid()) {
            OMemberProxy memberProxy = m.getMemberProxy();
            memberProxy.passwordHash = m.getPasswordHash();
            
            ofy().save().entity(memberProxy).now();
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
