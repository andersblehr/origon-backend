package com.scolaapp.api.auth;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
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
import com.scolaapp.api.aux.ScMeta;
import com.scolaapp.api.aux.ScURLParams;
import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScMember;
import com.scolaapp.api.model.proxy.ScMemberProxy;


@Path("auth")
public class ScAuthHandler
{
    private ScMeta m;
    
    
    private void sendRegistrationMessage()
    {
        Session session = Session.getInstance(new Properties());

        try {
            Message msg = new MimeMessage(session);
            
            // TODO: Localise message
            // TODO: Provide URL directly to app on device
            msg.setFrom(new InternetAddress("ablehr@gmail.com")); // TODO: Need another email address!
            msg.addRecipient(Message.RecipientType.TO, m.getEmailAddress());
            msg.setSubject("Complete your registration with Scola");
            msg.setText(String.format("Registration code: %s", m.getRegistrationCode()));
            
            Transport.send(msg);
            
            ScLog.log().fine(m.meta() + "Sent registration code to new Scola user.");
        } catch (Exception e) {
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GET
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @QueryParam (ScURLParams.DEVICE_ID)     String deviceId,
                                 @QueryParam (ScURLParams.DEVICE_TYPE)   String deviceType,
                                 @QueryParam (ScURLParams.APP_VERSION)   String appVersion,
                                 @QueryParam (ScURLParams.NAME)          String name)
    {
        m = new ScMeta(deviceId, deviceType, appVersion);
        
        ScAuthInfo authInfo = null;
        
        m.validateAuthorizationHeader(authorizationHeader, ScAuthPhase.REGISTRATION);
        m.validateName(name);
        
        if (m.isValid()) {
            authInfo = m.getAuthInfo();
            
            if (!authInfo.isRegistered) {
                m.getDAO().ofy().put(authInfo);
                
                sendRegistrationMessage();
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        return Response.status(HttpServletResponse.SC_OK).entity(authInfo).build();
    }
    
    
    @GET
    @Path("confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                @QueryParam (ScURLParams.AUTH_TOKEN)    String authToken,
                                @QueryParam (ScURLParams.SCOLA)         String scolaId,
                                @QueryParam (ScURLParams.DEVICE_ID)     String deviceId,
                                @QueryParam (ScURLParams.DEVICE_TYPE)   String deviceType,
                                @QueryParam (ScURLParams.APP_VERSION)   String appVersion)
    {
        m = new ScMeta(scolaId, deviceId, deviceType, appVersion);
        
        Date now = new Date();
        List<ScCachedEntity> scolaEntities = null;
        
        m.validateAuthorizationHeader(authorizationHeader, ScAuthPhase.CONFIRMATION);
        
        if (m.isValid()) {
            ScAuthInfo authInfo = m.getAuthInfo();
            
            if (authInfo.passwordHash.equals(m.getPasswordHash())) {
                ScDAO DAO = m.getDAO();
                
                DAO.ofy().delete(authInfo);
                DAO.putAuthToken(authToken);
                
                scolaEntities = DAO.fetchEntities();
            } else {
                ScLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (scolaEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(scolaEntities).lastModified(now).build();
        } else {
            return Response.status(HttpServletResponse.SC_NO_CONTENT).lastModified(now).build();
        }
    }
    
    
    @GET
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@HeaderParam(HttpHeaders.AUTHORIZATION)     String authorizationHeader,
                              @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date   lastFetchDate,
                              @QueryParam (ScURLParams.AUTH_TOKEN)        String authToken,
                              @QueryParam (ScURLParams.DEVICE_ID)         String deviceId,
                              @QueryParam (ScURLParams.DEVICE_TYPE)       String deviceType,
                              @QueryParam (ScURLParams.APP_VERSION)       String appVersion)
    {
        m = new ScMeta(deviceId, deviceType, appVersion);
        
        Date now = new Date(); 
        List<ScCachedEntity> scolaEntities = null;
        
        m.validateAuthorizationHeader(authorizationHeader, ScAuthPhase.LOGIN);
        
        if (m.isValid()) {
            ScMemberProxy memberProxy = m.getMemberProxy();
            
            if (memberProxy != null) {
                ScDAO DAO = m.getDAO();
                ScMember scolaMember = DAO.get(memberProxy.memberKey);
                
                if (scolaMember.passwordHash.equals(m.getPasswordHash())) {
                    DAO.putAuthToken(authToken);
                    //scolaEntities = DAO.fetchEntities(lastFetchDate);
                    scolaEntities = DAO.fetchEntities(); // TODO: Remove this line and comment back in line above
                } else {
                    ScLog.log().warning(m.meta() + "Incorrect password, raising UNAUTHORIZED (401).");
                    ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                ScLog.log().warning(m.meta() + String.format("User does not exist (%s), raising UNAUTHORIZED (401).", m.getUserId()));
                ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            ScLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            ScLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (scolaEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(scolaEntities).lastModified(now).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(now).build();
        }
    }
}
