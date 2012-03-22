package com.scolaapp.api.auth;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.objectify.NotFoundException;

import com.scolaapp.api.model.ScCachedEntity;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.utils.ScCrypto;
import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("auth")
public class ScAuthHandler
{
    private static final char[] symbols = new char[36];
    
    private final int kRegistrationCodeLength = 6;
    private final int kMinimumPasswordLength = 6;

    private ScDAO DAO;
    
    private InternetAddress email;
    private ScAuthInfo authInfo;
    private String authId;
    private String authPassword;
    
    
    static
    {
        for (int i = 0; i < 10; i++) {
            symbols[i] = (char)('0' + i);
        }
        
        for (int i = 10; i < 36; i++) {
            symbols[i] = (char)('A' + (i - 10));
        }
    }
    
    
    private boolean isHTTPHeaderAuthValid(String HTTPHeaderAuth)
    {
        String authString = null;
        String[] authElements = {};
        
        boolean isValid = ((HTTPHeaderAuth != null) && (HTTPHeaderAuth.indexOf("Basic ") == 0));
        
        if (isValid) {
            String base64EncodedAuthString = HTTPHeaderAuth.split(" ")[1];
            
            try {
                byte[] authBytes = Base64.decodeBase64(base64EncodedAuthString.getBytes("UTF-8"));
                authString = new String(authBytes, "UTF-8");
                
                authElements = authString.split(":");
                isValid = (authElements.length == 2);
            } catch (UnsupportedEncodingException e) {
                ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            ScLog.log().severe(DAO.meta() + String.format("Auth field 'Authorization: %s' from HTTP header does not conform with HTTP Basic Auth, expected 'Authorization: Basic <base64 encoded auth data>'. Barring entry for potential intruder, raising FORBIDDEN (403).", HTTPHeaderAuth));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        if (isValid) {
            authId = authElements[0];
            authPassword = authElements[1];
        } else {
            ScLog.log().severe(DAO.meta() + String.format("Decoded auth string '%s' does not conform with HTTP Basic Auth, expected '<id>:<password>'. Barring entry for potential intruder, raising FORBIDDEN (403).", authString));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    

    private boolean isDeviceIdValid(String deviceId)
    {
        boolean isValid = ((deviceId != null) && (deviceId.length() == 36));
        
        if (isValid) {
            String[] UUIDElements = deviceId.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && (UUIDElements[0].length() ==  8);
            isValid = isValid && (UUIDElements[1].length() ==  4);
            isValid = isValid && (UUIDElements[2].length() ==  4);
            isValid = isValid && (UUIDElements[3].length() ==  4);
            isValid = isValid && (UUIDElements[4].length() == 12);
        }
        
        if (isValid) {
            authInfo.deviceId = deviceId;
        } else {
            ScLog.log().severe(DAO.meta() + String.format("Invalid device ID: %s. Barring entry for potential intruder, raising FORBIDDEN (403).", deviceId));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private boolean isEmailValid(String emailAsEntered)
    {
        try {
            email = new InternetAddress(emailAsEntered);
            authInfo.email = emailAsEntered;
            
            try {
                ScScolaMember member = DAO.ofy().get(ScScolaMember.class, emailAsEntered);
                
                authInfo.isListed = true;
                authInfo.isRegistered = member.didRegister;
            } catch (NotFoundException e) {
                authInfo.isListed = false;
                authInfo.isRegistered = false;
            }
        } catch (AddressException e) {
            ScLog.log().info(DAO.meta() + String.format("'%s' is not a valid email address, raising UNAUTHORIZED (403).", emailAsEntered));
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_UNAUTHORIZED, String.format("invalidEmail(%s)", emailAsEntered));
        } 
        
        return true;
    }
    
    
    private boolean isPasswordValid(String passwordAsEntered)
    {
        boolean isValid = ((passwordAsEntered != null) && (passwordAsEntered.length() >= kMinimumPasswordLength));
        
        if (isValid) {
            String passwordHash = ScCrypto.generatePasswordHash(passwordAsEntered, authInfo.email);
            
            if (!authInfo.isRegistered) {
                authInfo.passwordHash = passwordHash;
                authInfo.isAuthenticated = false;
            } else {
                authInfo.isAuthenticated = passwordHash.equals(authInfo.passwordHash);
            }
        } else {
            ScLog.log().severe(DAO.meta() + String.format("Password '%s' is too short. Barring entry for potential intruder, raising FORBIDDEN (403).", passwordAsEntered));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private boolean isNameValid(String nameAsEntered)
    {
        boolean isValid = (nameAsEntered != null) && (nameAsEntered.length() > 0) && (nameAsEntered.indexOf(" ") > 0);
        
        if (isValid) {
            authInfo.name = nameAsEntered;
        } else {
            ScLog.log().severe(DAO.meta() + String.format("'%s' is not a full name. Barring entry for potential intruder, raising FORBIDDEN (403).", nameAsEntered));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private String generateRegistrationCode()
    {
        Random randomiser = new Random();
        char[] randomChars = new char[kRegistrationCodeLength];
        
        for (int i=0; i < kRegistrationCodeLength; i++) {
            randomChars[i] = symbols[randomiser.nextInt(symbols.length)];
        }
        
        return new String(randomChars);
    }
    
    
    private void sendRegistrationMessage()
    {
        Session session = Session.getInstance(new Properties());

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("ablehr@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, email);
            
            // TODO: Localise message
            // TODO: Provide URL directly to app on device
            msg.setSubject("Complete your registration with Scola");
            msg.setText(String.format("Registration code: %s", authInfo.registrationCode));
            
            Transport.send(msg);
            
            ScLog.log().fine(DAO.meta() + String.format("Sent registration code %s to new Scola user %s.", authInfo.registrationCode, authInfo.email));
        } catch (Exception e) {
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GET
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public Response registerUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                 @QueryParam ("duid")          String deviceId,
                                 @QueryParam ("device")        String deviceType,
                                 @QueryParam ("version")       String appVersion,
                                 @QueryParam ("name")          String name)
    {
        DAO = new ScDAO(deviceId, deviceType, appVersion, false);
        
        authInfo = new ScAuthInfo();
        
        boolean isValid = isHTTPHeaderAuthValid(HTTPHeaderAuth);
        
        isValid = isValid && isDeviceIdValid(deviceId);
        isValid = isValid && isEmailValid(authId);
        isValid = isValid && isPasswordValid(authPassword);
        isValid = isValid && isNameValid(name);
        
        if (isValid && !authInfo.isRegistered) {
            authInfo.registrationCode = generateRegistrationCode();
            DAO.ofy().put(authInfo);
            
            sendRegistrationMessage();
        }
        
        return Response.status(HttpServletResponse.SC_OK).entity(authInfo).build();
    }
    
    
    @GET
    @Path("confirm")
    public Response confirmUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                @QueryParam ("duid")          String deviceId,
                                @QueryParam ("device")        String deviceType,
                                @QueryParam ("version")       String appVersion,
                                @QueryParam ("token")         String authToken)
    {
        DAO = new ScDAO(deviceId, deviceType, appVersion, false);
        
        boolean willConfirmUser = false;
        List<ScCachedEntity> scolaEntities = null;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            authInfo = DAO.getOrThrow(ScAuthInfo.class, deviceId);
            
            willConfirmUser = ScCrypto.generatePasswordHash(authPassword, authId).equals(authInfo.passwordHash);
        }
        
        if (willConfirmUser) {
            DAO.putAuthToken(authToken, deviceId, authId);
            scolaEntities = DAO.fetchEntities(authToken, deviceId, null);
        } else {
            ScLog.log().severe(DAO.meta() + String.format("Cannot authenticate user from auth header '%s' (id: %s; pwd: %s). Barring entry for otential intruder, raising FORBIDDEN (403).", HTTPHeaderAuth, authId, authPassword));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        ScLog.log().fine(DAO.meta() + "Returning entities: " + scolaEntities.toString());
        return Response.status(HttpServletResponse.SC_OK).entity(scolaEntities).build();
    }
    
    
    @GET
    @Path("login")
    public Response loginUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                              @QueryParam ("duid")          String deviceId,
                              @QueryParam ("device")        String deviceType,
                              @QueryParam ("version")       String appVersion,
                              @QueryParam ("token")         String authToken,
                              @QueryParam ("lastFetch")     Date lastFetchDate)
    {
        DAO = new ScDAO(deviceId, deviceType, appVersion);

        boolean isAuthenticated = false;
        ScScolaMember member = null;
        List<ScCachedEntity> scolaEntities = null;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            member = DAO.get(ScScolaMember.class, authId);
            
            isAuthenticated = (member != null) && ScCrypto.generatePasswordHash(authPassword, authId).equals(member.passwordHash);
        }
        
        if (isAuthenticated) {
            DAO.putAuthToken(authToken, deviceId, authId);
            scolaEntities = DAO.fetchEntities(authToken, deviceId, lastFetchDate);
        } else {
            ScLog.log().warning(DAO.meta() + String.format("User %s failed to authenticate, raising UNAUTHORIZED (401).", authId));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        return Response.status(HttpServletResponse.SC_OK).entity(scolaEntities).build();
    }
}
