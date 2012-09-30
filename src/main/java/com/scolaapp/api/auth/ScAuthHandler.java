package com.scolaapp.api.auth;

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

import com.scolaapp.api.aux.ScDAO;
import com.scolaapp.api.aux.ScLog;
import com.scolaapp.api.aux.ScMemberProxy;
import com.scolaapp.api.aux.ScMeta;
import com.scolaapp.api.aux.ScURLParams;
import com.scolaapp.api.model.ScCachedEntity;


@Path("auth")
public class ScAuthHandler
{
    private ScMeta m;
    
    
    @GET
    @Path("activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @QueryParam(ScURLParams.AUTH_TOKEN) String authToken,
                                 @QueryParam(ScURLParams.DEVICE_ID) String deviceId,
                                 @QueryParam(ScURLParams.DEVICE_TYPE) String deviceType,
                                 @QueryParam(ScURLParams.APP_VERSION) String appVersion)
    {
        m = new ScMeta(deviceId, deviceType, appVersion);
        
        m.validateAuthorizationHeader(authorizationHeader, ScAuthPhase.ACTIVATION);
        m.validateAuthToken(authToken);
        
        Date fetchDate = new Date();
        List<ScCachedEntity> scolaEntities = null;
        
        if (m.isValid()) {
            ScAuthInfo authInfo = m.getAuthInfo();
            
            if (authInfo.passwordHash.equals(m.getPasswordHash())) {
                ScDAO DAO = m.getDAO();
                
                DAO.ofy().delete(authInfo);
                DAO.putAuthToken(authToken);
                
                scolaEntities = DAO.fetchEntities(null);
            } else {
                ScLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (scolaEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(scolaEntities).lastModified(fetchDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NO_CONTENT).lastModified(fetchDate).build();
        }
    }
    
    
    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date lastFetchDate,
                              @QueryParam(ScURLParams.AUTH_TOKEN) String authToken,
                              @QueryParam(ScURLParams.DEVICE_ID) String deviceId,
                              @QueryParam(ScURLParams.DEVICE_TYPE) String deviceType,
                              @QueryParam(ScURLParams.APP_VERSION) String appVersion)
    {
        m = new ScMeta(deviceId, deviceType, appVersion);
        
        m.validateAuthorizationHeader(authorizationHeader, ScAuthPhase.LOGIN);
        m.validateAuthToken(authToken);
        
        ScAuthInfo authInfo = null;
        List<ScCachedEntity> fetchedEntities = null;
        Date fetchDate = new Date(); 
        
        if (m.isValid()) {
            ScMemberProxy memberProxy = m.getMemberProxy();
            
            if ((memberProxy == null) || !memberProxy.didRegister) {
                authInfo = m.getAuthInfo();
                m.getDAO().ofy().put(authInfo);
                
                Session session = Session.getInstance(new Properties());

                try {
                    Message message = new MimeMessage(session);
                    
                    // TODO: Localise message
                    // TODO: Provide URL directly to app on device
                    message.setFrom(new InternetAddress("ablehr@gmail.com")); // TODO: Need another email address!
                    message.addRecipient(Message.RecipientType.TO, m.getEmailAddress());
                    message.setSubject("Complete your registration with Scola");
                    message.setText(String.format("Activation code: %s", m.getActivationCode()));
                    
                    Transport.send(message);
                    
                    ScLog.log().fine(m.meta() + "Sent activation code to new Scola user.");
                } catch (MessagingException e) {
                    ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                if ((memberProxy != null) && memberProxy.didRegister) {
                    if (memberProxy.passwordHash.equals(m.getPasswordHash())) {
                        m.getDAO().putAuthToken(authToken);
                        
                        fetchedEntities = m.getDAO().fetchEntities(lastFetchDate);
                    } else {
                        ScLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                        ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                } else {
                    ScLog.log().warning(m.meta() + "User is inactive or does not exist, raising UNAUTHORIZED (401).");
                    ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (authInfo != null) {
            return Response.status(HttpServletResponse.SC_CREATED).entity(authInfo).build();
        } else if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(fetchedEntities).lastModified(fetchDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(fetchDate).build();
        }
    }
}
