package com.origoapp.api.auth;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.origoapp.api.aux.ODAO;
import com.origoapp.api.aux.OLog;
import com.origoapp.api.aux.OMemberProxy;
import com.origoapp.api.aux.OMeta;
import com.origoapp.api.aux.OURLParams;
import com.origoapp.api.model.OReplicatedEntity;


@Path("auth")
public class OAuthHandler
{
    private OMeta m;
    
    
    @GET
    @Path("activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                 @QueryParam(OURLParams.DEVICE_ID) String deviceId,
                                 @QueryParam(OURLParams.DEVICE_TYPE) String deviceType,
                                 @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        m = new OMeta(deviceId, deviceType, appVersion);
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.ACTIVATE);
        m.validateAuthToken(authToken);
        
        List<OReplicatedEntity> fetchedEntities = null;
        Date replicationDate = new Date();
        
        if (m.isValid()) {
            OAuthInfo authInfo = m.getAuthInfo();
            
            if (authInfo.passwordHash.equals(m.getPasswordHash())) {
                ODAO DAO = m.getDAO();
                
                DAO.ofy().delete(authInfo);
                DAO.putAuthToken(authToken);
                
                fetchedEntities = DAO.fetchEntities(null);
            } else {
                OLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).header(HttpHeaders.LOCATION, m.getUserId()).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NO_CONTENT).lastModified(replicationDate).build();
        }
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
        m = new OMeta(deviceId, deviceType, appVersion);
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.LOGIN);
        m.validateAuthToken(authToken);
        
        OAuthInfo authInfo = null;
        List<OReplicatedEntity> fetchedEntities = null;
        Date replicationDate = new Date(); 
        
        if (m.isValid()) {
            OMemberProxy memberProxy = m.getMemberProxy();
            
            if ((memberProxy == null) || !memberProxy.didSignUp) {
                authInfo = m.getAuthInfo();
                m.getDAO().ofy().put(authInfo);
                
                emailCode(OAuthPhase.SIGNUP);
            } else {
                if ((memberProxy != null) && memberProxy.didSignUp) {
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
            }
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (authInfo != null) {
            return Response.status(HttpServletResponse.SC_CREATED).entity(authInfo).build();
        } else if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).header(HttpHeaders.LOCATION, m.getUserId()).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("emailcode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailActivationCode(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                        @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                        @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        m = new OMeta(authToken, appVersion);
        
        m.validateAuthorizationHeader(authorizationHeader, OAuthPhase.EMAIL_CODE);
        
        OAuthInfo authInfo = null;
        
        if (m.isValid()) {
            authInfo = m.getAuthInfo();
            emailCode(OAuthPhase.EMAIL_CODE);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return Response.status(HttpServletResponse.SC_CREATED).entity(authInfo).build();
    }


    private void emailCode(OAuthPhase authPhase)
    {
        Session session = Session.getInstance(new Properties());
    
        try {
            Message message = new MimeMessage(session);
            
            // TODO: Localise message
            // TODO: Provide URL directly to app on device
            message.setFrom(new InternetAddress("ablehr@gmail.com")); // TODO: Need another email address!
            message.addRecipient(Message.RecipientType.TO, m.getEmailAddress());
            
            if (authPhase == OAuthPhase.SIGNUP) {
                message.setSubject("Complete your registration with Origo");
                message.setText(String.format("Activation code: %s", m.getActivationCode()));
                
                OLog.log().fine(m.meta() + "Sent activation code to new Origo user.");
            } else if (authPhase == OAuthPhase.EMAIL_CODE) {
                message.setSubject("Activate your email address for use with Origo");
                message.setText(String.format("Activation code: %s", m.getActivationCode()));
                
                OLog.log().fine(m.meta() + "Sent email activation code to " + m.getEmail() + ".");
            }
            
            Transport.send(message);
        } catch (MessagingException e) {
            OLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
