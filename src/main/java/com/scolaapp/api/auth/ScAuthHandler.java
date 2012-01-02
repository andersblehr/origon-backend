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
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import com.scolaapp.api.model.ScPerson;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.utils.ScAppEnv;
import com.scolaapp.api.utils.ScCrypto;


@Path("auth")
public class ScAuthHandler
{
    private static final char[] symbols = new char[36];
    
    private final int registrationCodeLength = 6;
    private final int minimumPasswordLength  = 6;
    
    private ScAuthState authState;
    private Exception servletException;
    private int servletStatusCode;
    private String servletReason;

    private InternetAddress userInternetAddress;
    
    
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
            authState.deviceUUID = UUID;
        } else {
            ScAppEnv.getLog().severe(String.format("Invalid UUID: %s. Potential intruder, barring entry.", UUID));
            servletStatusCode = HttpServletResponse.SC_FORBIDDEN;
        }
        
        return isValid;
    }
    
    
    private boolean isNameValid(String name)
    {
        boolean isValid = (name != null) && (name.length() > 0) && (name.indexOf(" ") >0);
        
        if (isValid) {
            authState.userFullName = name;
        } else {
            ScAppEnv.getLog().severe(String.format("'%s' is not a full name. Potential intruder, barring entry.", name));
            servletStatusCode = HttpServletResponse.SC_FORBIDDEN;
        }
        
        return isValid;
    }
    
    
    private boolean isEmailValid(String email)
    {
        boolean isValid = true;
        
        try {
            userInternetAddress = new InternetAddress(email);
            authState.userEmail = email;
            
            try {
                ScPerson person = ScAppEnv.ofy().get(ScPerson.class, email);
                
                authState.isListed = true;
                authState.isActive = person.isActive;
            } catch (NotFoundException e) {
                authState.isListed = false;
                authState.isActive = false;
            }
        } catch (AddressException e) {
            ScAppEnv.getLog().info(String.format("'%s' is not a valid email address.", email));
            servletException = e;
            servletStatusCode = HttpServletResponse.SC_UNAUTHORIZED;
            servletReason = "email";
            
            isValid = false;
        } 
        
        return isValid;
    }
    
    
    private boolean isScolaShortnameValid(String scolaShortname)
    {
        boolean isValid = true;

        try {
            ScScola scola = ScAppEnv.ofy().get(ScScola.class, scolaShortname);
            Map<Key<ScPerson>, ScPerson> scolaMembers = ScAppEnv.ofy().get(Arrays.asList(scola.members));
            
            for (Key<ScPerson> scolaMemberKey: scola.members) {
                ScPerson person = scolaMembers.get(scolaMemberKey);
                
                if (person.name.equals(authState.userFullName)) {
                    authState.isListed = true;
                    authState.isActive = person.isActive;
                    authState.userEmail = person.email;
                    
                    break;
                }
            }
            
            try {
                userInternetAddress = new InternetAddress(authState.userEmail);
            } catch (AddressException e) {
                ScAppEnv.getLog().severe(String.format("BROKEN: Email '%s' registered for user '%' in scola with shortname '%s' is not valid. This should not happen.", authState.userEmail, authState.userFullName, scolaShortname));
                servletException = e;
                servletStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                servletReason = "email";
                
                isValid = false;
            }
        } catch (NotFoundException e) {
            ScAppEnv.getLog().info(String.format("Scola with shortname '%s' does not exist, please check spelling.", scolaShortname));
            servletException = e;
            servletStatusCode = HttpServletResponse.SC_NOT_FOUND;
            servletReason = "scolaShortname";
            
            isValid = false;
        }
        
        if (isValid && !authState.isListed) {
            ScAppEnv.getLog().info(String.format("Scola with shortname '%s' does not have an invitation for '%s', please check spelling of name.", scolaShortname, authState.userFullName));
            servletStatusCode = HttpServletResponse.SC_NOT_FOUND;
            servletReason = "name";
            
            isValid = false;
        }
        
        return isValid;
    }
    
    
    private boolean isPasswordValid(String password)
    {
        boolean isValid = ((password != null) && (password.length() >= minimumPasswordLength));
        
        if (isValid) {
            authState.passwordHash = ScCrypto.generatePasswordHash(password, authState.userEmail);
        } else {
            ScAppEnv.getLog().severe(String.format("Password '%s' is too short. Potential intruder, barring entry.", password));
            servletStatusCode = HttpServletResponse.SC_FORBIDDEN;
        }
        
        return isValid;
    }
    
    
    private boolean isHTTPHeaderAuthValid(String HTTPHeaderAuth)
    {
        String authString = null;
        String[] authElements = {};
        
        boolean isValid = ((HTTPHeaderAuth != null) && (HTTPHeaderAuth.indexOf("Basic ") == 0));
        
        if (!isValid) {
            ScAppEnv.getLog().severe(String.format("Auth field 'Authorization: %s' from HTTP header does not conform with HTTP Basic Auth, expected 'Authorization: Basic <base64 encoded auth data>'. Potential intruder, barring entry.", HTTPHeaderAuth));
            servletStatusCode = HttpServletResponse.SC_FORBIDDEN;
        }

        if (isValid) {
            String base64EncodedAuthString = HTTPHeaderAuth.split(" ")[1];
            
            try {
                byte[] authBytes = Base64.decodeBase64(base64EncodedAuthString.getBytes("UTF-8"));
                authString = new String(authBytes, "UTF-8");
                
                authElements = authString.split(":");
                isValid = (authElements.length == 2);
            
                if (!isValid) {
                    ScAppEnv.getLog().severe(String.format("Decoded auth string '%s' does not conform with HTTP Basic Auth, expected '<id>:<password>'. Potential intruder, barring entry.", authString));
                    servletStatusCode = HttpServletResponse.SC_FORBIDDEN;
                }
            } catch (UnsupportedEncodingException e) {
                ScAppEnv.getLog().severe("BROKEN: Caught UnsupportedEncodingException when decoding auth string, bailing out.");
                servletException = e;
                servletStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                servletReason = "auth";
                
                isValid = false;
            }
        }
        
        if (isValid) {
            if (authElements[0].indexOf("@") > 0) {
                isValid = isEmailValid(authElements[0]);
            } else {
                isValid = isScolaShortnameValid(authElements[0]);
            }
            
            isValid = isValid && isPasswordValid(authElements[1]);
        }
        
        return isValid;
    }
    

    private String generateRegistrationCode()
    {
        Random randomiser = new Random();
        char[] randomChars = new char[registrationCodeLength];
        
        for (int i=0; i < registrationCodeLength; i++) {
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
            msg.addRecipient(Message.RecipientType.TO, userInternetAddress);
            
            // TODO: Localise message
            // TODO: Provide URL directly to app on device
            msg.setSubject("Complete your registration with Scola");
            msg.setText(String.format("Registration code: %s", authState.registrationCode));
            
            Transport.send(msg);
            
            ScAppEnv.getLog().fine(String.format("Sent registration code %s to new Scola user %s.", authState.registrationCode, authState.userEmail));
        } catch (Exception e) {
            throw new WebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
     
    @GET
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public ScAuthState registerUser(@QueryParam ("uuid")          String deviceUUID,
                                    @QueryParam ("name")          String name,
                                    @HeaderParam("Authorization") String HTTPHeaderAuth)
    {
        authState = new ScAuthState();
        
        if (isUUIDValid(deviceUUID) && isNameValid(name) && isHTTPHeaderAuthValid(HTTPHeaderAuth)) {
            authState.registrationCode = generateRegistrationCode();
            sendRegistrationMessage();
            
            ScAppEnv.ofy().put(authState);
        } else {
            ResponseBuilderImpl responseBuilder = new ResponseBuilderImpl();
            responseBuilder.status(servletStatusCode);
            
            if (servletReason != null) {
                responseBuilder.header("reason", servletReason);
            }
            
            Response response = responseBuilder.build();
            
            if (servletException != null) {
                throw new WebApplicationException(servletException, response);
            } else {
                throw new WebApplicationException(response);
            }
        }
        
        return authState;
    }
}
