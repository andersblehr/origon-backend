package com.scolaapp.api.auth;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.ScScolaMember;
import com.scolaapp.api.utils.ScUtil;
import com.scolaapp.api.utils.ScCrypto;
import com.scolaapp.api.utils.ScDAO;


@Path("auth")
public class ScAuthHandler
{
    private static final char[] symbols = new char[36];
    
    private static final ScDAO DAO = new ScDAO();
    
    private final int kRegistrationCodeLength = 6;
    private final int kMinimumPasswordLength = 6;

    private InternetAddress email;
    private ScAuthInfo authInfo;
    private String authIdent;
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
    
    
    private boolean isUUIDValid(String UUID)
    {
        boolean isValid = ((UUID != null) && (UUID.length() == 36));
        
        if (isValid) {
            String[] UUIDElements = UUID.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && (UUIDElements[0].length() ==  8);
            isValid = isValid && (UUIDElements[1].length() ==  4);
            isValid = isValid && (UUIDElements[2].length() ==  4);
            isValid = isValid && (UUIDElements[3].length() ==  4);
            isValid = isValid && (UUIDElements[4].length() == 12);
        }
        
        if (isValid) {
            authInfo.deviceUUIDHash = ScCrypto.hashUsingSHA1(UUID);
        } else {
            ScUtil.log().severe(String.format("Invalid UUID: %s. Potential intruder, barring entry.", UUID));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
    }
    
    
    private boolean isNameValid(String name)
    {
        boolean isValid = (name != null) && (name.length() > 0) && (name.indexOf(" ") > 0);
        
        if (isValid) {
            authInfo.name = name;
        } else {
            ScUtil.log().severe(String.format("'%s' is not a full name. Potential intruder, barring entry.", name));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
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
                
                authInfo.isListed = true;
                authInfo.isActive = person.isActive;
            } catch (NotFoundException e) {
                authInfo.isListed = false;
                authInfo.isActive = false;
            }
        } catch (AddressException e) {
            ScUtil.log().info(String.format("'%s' is not a valid email address.", email_));
            ScUtil.throwWebApplicationException(e, HttpServletResponse.SC_UNAUTHORIZED, "email");
        } 
        
        return true;
    }
    
    
    private boolean isScolaShortnameValid(String scolaShortname)
    {
        boolean isValid = true;

        try {
            ScScola scola = DAO.ofy().get(ScScola.class, scolaShortname);
            Map<Key<ScPerson>, ScPerson> scolaMembers = DAO.ofy().get(Arrays.asList(scola.members));
            
            for (Key<ScPerson> scolaMemberKey: scola.members) {
                ScPerson person = scolaMembers.get(scolaMemberKey);
                
                if (person.name.equals(authInfo.name)) {
                    authInfo.isListed = true;
                    authInfo.isActive = person.isActive;
                    authInfo.email = person.email;
                    
                    break;
                }
            }
        } catch (NotFoundException e) {
            ScUtil.log().info(String.format("Scola with shortname '%s' does not exist, please check spelling.", scolaShortname));
            ScUtil.throwWebApplicationException(e, HttpServletResponse.SC_NOT_FOUND, "scola");
        }
            
        try {
            email = new InternetAddress(authInfo.email);
        } catch (AddressException e) {
            ScUtil.log().severe(String.format("BROKEN: Email '%s' registered for user '%' in scola with shortname '%s' is not valid. This should not happen.", authInfo.email, authInfo.name, scolaShortname));
            ScUtil.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "email");
        }
    
        if (isValid && !authInfo.isListed) {
            ScUtil.log().info(String.format("Scola with shortname '%s' does not have an invitation for '%s', please check spelling of name.", scolaShortname, authInfo.name));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_NOT_FOUND, "name");
        }
        
        return isValid;
    }
    
    
    private boolean isPasswordValid(String password)
    {
        boolean isValid = ((password != null) && (password.length() >= kMinimumPasswordLength));
        
        if (isValid) {
            authInfo.passwordHash = ScCrypto.generatePasswordHash(password, authInfo.email);
        } else {
            ScUtil.log().severe(String.format("Password '%s' is too short. Potential intruder, barring entry.", password));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return isValid;
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
                ScUtil.log().severe("BROKEN: Caught UnsupportedEncodingException when decoding auth string, bailing out.");
                ScUtil.throwWebApplicationException(e, HttpServletResponse.SC_FORBIDDEN, "auth");
            }
        } else {
            ScUtil.log().severe(String.format("Auth field 'Authorization: %s' from HTTP header does not conform with HTTP Basic Auth, expected 'Authorization: Basic <base64 encoded auth data>'. Potential intruder, barring entry.", HTTPHeaderAuth));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        if (isValid) {
            authIdent = authElements[0];
            authPassword = authElements[1];
        } else {
            ScUtil.log().severe(String.format("Decoded auth string '%s' does not conform with HTTP Basic Auth, expected '<id>:<password>'. Potential intruder, barring entry.", authString));
            ScUtil.throwWebApplicationException(HttpServletResponse.SC_FORBIDDEN);
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
            
            ScUtil.log().fine(String.format("Sent registration code %s to new Scola user %s.", authInfo.registrationCode, authInfo.email));
        } catch (Exception e) {
            ScUtil.throwWebApplictionException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GET
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public ScAuthInfo registerUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                   @QueryParam ("name")          String name,
                                   @QueryParam ("uuid")          String deviceUUID)
    {
        authInfo = new ScAuthInfo();
        
        boolean isValid = isHTTPHeaderAuthValid(HTTPHeaderAuth);
        
        isValid = isValid && ((authIdent.indexOf("@") > 0) ? isEmailValid(authIdent) : isScolaShortnameValid(authIdent));
        isValid = isValid && isPasswordValid(authPassword);
        isValid = isValid && isNameValid(name);
        isValid = isValid && isUUIDValid(deviceUUID);
        
        if (isValid) {
            authInfo.registrationCode = generateRegistrationCode();
            DAO.ofy().put(authInfo);
            
            sendRegistrationMessage();
        }
        
        return authInfo;
    }
    
    
    @GET
    @Path("confirm")
    @Produces({MediaType.APPLICATION_JSON})
    public ScScolaMember confirmUser(@HeaderParam("Authorization") String HTTPHeaderAuth,
                                     @QueryParam ("uuid")          String deviceUUIDHash)
    {
        boolean willConfirmUser = false;
        ScScolaMember member = null;
        
        if (isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            authInfo = DAO.getOrThrow(ScAuthInfo.class, authIdent);
            
            willConfirmUser = ScCrypto.generatePasswordHash(authPassword, authIdent).equals(authInfo.passwordHash);
            willConfirmUser = willConfirmUser && deviceUUIDHash.equals(authInfo.deviceUUIDHash);
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
            ScUtil.log().severe(String.format("Cannot authenticate user from auth header '%s' (id: %s; pwd: %s). Potential intruder, barring entry.", HTTPHeaderAuth, authIdent, authPassword));
            throw new WebApplicationException(HttpServletResponse.SC_FORBIDDEN);
        }
        
        return member;
    }
}
