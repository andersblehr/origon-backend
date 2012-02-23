package com.scolaapp.api.auth;

import java.io.UnsupportedEncodingException;
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

import org.apache.commons.codec.binary.Base64;

import com.googlecode.objectify.NotFoundException;

import com.scolaapp.api.model.ScDevice;
import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.utils.ScLog;
import com.scolaapp.api.utils.ScCrypto;
import com.scolaapp.api.utils.ScDAO;


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
    
    private String deviceId;

    
    static
    {
        for (int i = 0; i < 10; i++) {
            symbols[i] = (char)('0' + i);
        }
        
        for (int i = 10; i < 36; i++) {
            symbols[i] = (char)('a' + (i - 10));
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
                ScLog.log().severe(String.format("%s BROKEN: Caught UnsupportedEncodingException when decoding auth string, bailing out.", ScLog.meta(deviceId)));
                ScLog.throwWebApplicationException(e, HttpServletResponse.SC_FORBIDDEN, String.format("invalidAuthString(%s)", authString));
            }
        } else {
            ScLog.log().severe(String.format("%s Auth field 'Authorization: %s' from HTTP header does not conform with HTTP Basic Auth, expected 'Authorization: Basic <base64 encoded auth data>'. Potential intruder, barring entry.", ScLog.meta(deviceId), HTTPHeaderAuth));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        if (isValid) {
            authId = authElements[0];
            authPassword = authElements[1];
        } else {
            ScLog.log().severe(String.format("%s Decoded auth string '%s' does not conform with HTTP Basic Auth, expected '<id>:<password>'. Potential intruder, barring entry.", ScLog.meta(deviceId), authString));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    

    private boolean isUUIDValid(String deviceUUID)
    {
        boolean isValid = ((deviceUUID != null) && (deviceUUID.length() == 36));
        
        if (isValid) {
            String[] UUIDElements = deviceUUID.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && (UUIDElements[0].length() ==  8);
            isValid = isValid && (UUIDElements[1].length() ==  4);
            isValid = isValid && (UUIDElements[2].length() ==  4);
            isValid = isValid && (UUIDElements[3].length() ==  4);
            isValid = isValid && (UUIDElements[4].length() == 12);
        }
        
        if (isValid) {
            authInfo.deviceUUID = deviceUUID;
            ScDevice device = DAO.get(ScDevice.class, authInfo.deviceUUID);
            
            if (device != null) {
                authInfo.isDeviceListed = true;
            }
        } else {
            ScLog.log().severe(String.format("%s Invalid UUID: %s. Potential intruder, barring entry.", ScLog.meta(deviceId), authInfo.deviceUUID));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private boolean isEmailValid(String email_)
    {
        try {
            email = new InternetAddress(email_);
            authInfo.email = email_;
            
            try {
                ScPerson person = DAO.ofy().get(ScPerson.class, email_);
                person.household = DAO.ofy().get(person.householdKey);
                
                authInfo.isListed = true;
                authInfo.isActive = person.isActive;
                authInfo.listedPerson = person;
            } catch (NotFoundException e) {
                authInfo.isListed = false;
                authInfo.isActive = false;
            }
            
            if (authInfo.isActive) {
                ScScolaMember member = DAO.getOrThrow(ScScolaMember.class, authInfo.email);
                authInfo.passwordHash = member.passwordHash;
            }
        } catch (AddressException e) {
            ScLog.log().info(String.format("%s '%s' is not a valid email address.", ScLog.meta(deviceId), email_));
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_UNAUTHORIZED, String.format("invalidEmail(%s)", email_));
        } 
        
        return true;
    }
    
    
    private boolean isPasswordValid(String password)
    {
        boolean isValid = ((password != null) && (password.length() >= kMinimumPasswordLength));
        
        if (isValid) {
            String passwordHash = ScCrypto.generatePasswordHash(password, authInfo.email);
            
            if (!authInfo.isActive) {
                authInfo.passwordHash = passwordHash;
                authInfo.isAuthenticated = false;
            } else {
                authInfo.isAuthenticated = passwordHash.equals(authInfo.passwordHash);
            }
        } else {
            ScLog.log().severe(String.format("%s Password '%s' is too short. Potential intruder, barring entry.", ScLog.meta(deviceId), password));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private boolean isNameValid(String name)
    {
        boolean isValid = (name != null) && (name.length() > 0) && (name.indexOf(" ") > 0);
        
        if (isValid) {
            authInfo.name = name;
        } else {
            ScLog.log().severe(String.format("%s '%s' is not a full name. Potential intruder, barring entry.", ScLog.meta(deviceId), name));
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
            
            ScLog.log().fine(String.format("%s Sent registration code %s to new Scola user %s.", ScLog.meta(deviceId), authInfo.registrationCode, authInfo.email));
        } catch (Exception e) {
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GET
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public ScAuthInfo registerUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                   @QueryParam ("duid")          String deviceUUID,
                                   @QueryParam ("device")        String deviceType,
                                   @QueryParam ("version")       String appVersion,
                                   @QueryParam ("name")          String name)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);
        
        ScLog.log().fine(String.format("%s Registering new user.", ScLog.meta(deviceId)));
        
        authInfo = new ScAuthInfo();
        boolean isValid = isHTTPHeaderAuthValid(HTTPHeaderAuth);
        
        isValid = isValid && isUUIDValid(deviceUUID);
        isValid = isValid && isEmailValid(authId);
        isValid = isValid && isPasswordValid(authPassword);
        isValid = isValid && isNameValid(name);
        
        if (isValid && !authInfo.isAuthenticated && !authInfo.isActive) {
            authInfo.registrationCode = generateRegistrationCode();
            DAO.ofy().put(authInfo);
            
            sendRegistrationMessage();
        }
        
        return authInfo;
    }
    
    
    @GET
    @Path("confirm")
    public void confirmUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                            @QueryParam ("duid")          String deviceUUID,
                            @QueryParam ("device")        String deviceType,
                            @QueryParam ("version")       String appVersion)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);
        
        ScLog.log().fine(String.format("%s Confirming and logging in new user.", ScLog.meta(deviceId)));
        
        boolean willConfirmUser = false;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            authInfo = DAO.getOrThrow(ScAuthInfo.class, authId);
            
            willConfirmUser = ScCrypto.generatePasswordHash(authPassword, authId).equals(authInfo.passwordHash);
            willConfirmUser = willConfirmUser && deviceUUID.equals(authInfo.deviceUUID);
        }
        
        if (willConfirmUser) {
            DAO.ofy().delete(authInfo);
        } else {
            ScLog.log().severe(String.format("%s Cannot authenticate user from auth header '%s' (id: %s; pwd: %s). Potential intruder, barring entry.", ScLog.meta(deviceId), HTTPHeaderAuth, authId, authPassword));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
    }
    
    
    @GET
    @Path("login")
    public void loginUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                          @QueryParam ("duid")          String deviceUUID,
                          @QueryParam ("device")        String deviceType,
                          @QueryParam ("version")       String appVersion)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);

        ScLog.log().fine(String.format("%s Attempting to log in user.", ScLog.meta(deviceId)));
        
        ScScolaMember member = null;
        boolean isAuthenticated = false;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            member = DAO.get(ScScolaMember.class, authId);
            
            isAuthenticated = (member != null) && ScCrypto.generatePasswordHash(authPassword, authId).equals(member.passwordHash);
        }
        
        if (isAuthenticated) {
            ScLog.log().info(String.format("%s Authenticated user with id '%s'.", ScLog.meta(deviceId), authId));
        } else {
            ScLog.log().warning(String.format("%s User with id '%s' failed to authenticate.", ScLog.meta(deviceId), authId));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
