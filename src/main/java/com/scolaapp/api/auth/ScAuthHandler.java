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
import com.scolaapp.api.model.ScDeviceListing;
import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.utils.ScAppEnv;
import com.scolaapp.api.utils.ScLog;
import com.scolaapp.api.utils.ScCrypto;
import com.scolaapp.api.utils.ScDAO;


@Path("auth")
public class ScAuthHandler
{
    private static final char[] symbols = new char[36];
    
    private final int kRegistrationCodeLength = 6;
    private final int kMinimumPasswordLength = 6;

    private ScAppEnv env;
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
                ScLog.severe(env, "BROKEN: Caught UnsupportedEncodingException when decoding auth string, bailing out.");
                ScLog.throwWebApplicationException(e, HttpServletResponse.SC_FORBIDDEN, String.format("invalidAuthString(%s)", authString));
            }
        } else {
            ScLog.severe(env, String.format("Auth field 'Authorization: %s' from HTTP header does not conform with HTTP Basic Auth, expected 'Authorization: Basic <base64 encoded auth data>'. Potential intruder, barring entry.", HTTPHeaderAuth));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        if (isValid) {
            authId = authElements[0];
            authPassword = authElements[1];
        } else {
            ScLog.severe(env, String.format("Decoded auth string '%s' does not conform with HTTP Basic Auth, expected '<id>:<password>'. Potential intruder, barring entry.", authString));
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
            ScLog.severe(env, String.format("Invalid UUID: %s. Potential intruder, barring entry.", authInfo.deviceUUID));
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
                
                Iterable<ScDeviceListing> deviceListings = DAO.ofy().query(ScDeviceListing.class).filter("usedBy", member);
                
                for (ScDeviceListing deviceListing : deviceListings) {
                    ScDevice device = DAO.ofy().get(deviceListing.device);
                    
                    if (device.uuid.equals(authInfo.deviceUUID)) {
                        authInfo.isDeviceListed = true;
                        break;
                    }
                }
            }
        } catch (AddressException e) {
            ScLog.info(env, String.format("'%s' is not a valid email address.", email_));
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
            ScLog.severe(env, String.format("Password '%s' is too short. Potential intruder, barring entry.", password));
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
            ScLog.severe(env, String.format("'%s' is not a full name. Potential intruder, barring entry.", name));
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
            
            ScLog.fine(env, String.format("Sent registration code %s to new Scola user %s.", authInfo.registrationCode, authInfo.email));
        } catch (Exception e) {
            ScLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GET
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public ScAuthInfo registerUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                   @QueryParam ("version")       String appVersion,
                                   @QueryParam ("device")        String deviceType,
                                   @QueryParam ("uuid")          String deviceUUID,
                                   @QueryParam ("name")          String name)
    {
        env = ScAppEnv.env(appVersion, deviceType, deviceUUID);
        DAO = new ScDAO(env);
        
        ScLog.fine(env, "Registering new user.");
        
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
                            @QueryParam ("version")       String appVersion,
                            @QueryParam ("device")        String deviceType,
                            @QueryParam ("uuid")          String deviceUUID)
    {
        env = ScAppEnv.env(appVersion, deviceType, deviceUUID);
        DAO = new ScDAO(env);
        
        ScLog.fine(env, "Confirming and logging in new user.");
        
        ScScolaMember member = null;
        boolean willConfirmUser = false;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            authInfo = DAO.getOrThrow(ScAuthInfo.class, authId);
            
            willConfirmUser = ScCrypto.generatePasswordHash(authPassword, authId).equals(authInfo.passwordHash);
            willConfirmUser = willConfirmUser && deviceUUID.equals(authInfo.deviceUUID);
        }
        
        if (willConfirmUser) {
            ScPerson newUser;
            
            if (authInfo.isListed) {
                newUser = DAO.getOrThrow(ScPerson.class, authInfo.email);
            } else {
                newUser = new ScPerson(authInfo.email, authInfo.name);
            }
            
            member = new ScScolaMember(newUser, authInfo.passwordHash);
            member.isActive = true;
            
            DAO.ofy().put(member);
            DAO.ofy().delete(authInfo);
        } else {
            ScLog.severe(env, String.format("Cannot authenticate user from auth header '%s' (id: %s; pwd: %s). Potential intruder, barring entry.", HTTPHeaderAuth, authId, authPassword));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        //return member;
    }
    
    
    @GET
    @Path("login")
    public void loginUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                          @QueryParam ("version")       String appVersion,
                          @QueryParam ("device")        String deviceType,
                          @QueryParam ("uuid")          String deviceUUID)
    {
        env = ScAppEnv.env(appVersion, deviceType, deviceUUID);
        DAO = new ScDAO(env);

        ScLog.fine(env, "Attempting to log in user.");
        
        ScScolaMember member = null;
        boolean isAuthenticated = false;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            member = DAO.get(ScScolaMember.class, authId);
            
            isAuthenticated = (member != null) && ScCrypto.generatePasswordHash(authPassword, authId).equals(member.passwordHash);
        }
        
        if (isAuthenticated) {
            ScLog.info(env, String.format("Authenticated user with id '%s'.", authId));
        } else {
            ScLog.warning(env, String.format("User with id '%s' failed to authenticate.", authId));
            ScLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        //return member;
    }
}
