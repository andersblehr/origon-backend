package com.scolaapp.api.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.objectify.NotFoundException;
import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthPhase;
import com.scolaapp.api.auth.ScAuthToken;
import com.scolaapp.api.model.ScScolaMember;


public class ScMeta
{
    private final static char[] symbols = new char[36];
    
    private final static int kMinimumPasswordLength = 6;
    private final static int kRegistrationCodeLength = 6;
    
    public ScDAO DAO;
    
    public boolean isValid = true;
    
    public String name = null;
    public String userId = null;
    public String passwordHash = null;
    public String deviceId = null;
    public String registrationCode = null;
    
    public InternetAddress emailAddress = null;
    
    private String deviceType = null;
    private String appVersion = null;
    
    
    static
    {
        for (int i = 0; i < 10; i++) {
            symbols[i] = (char)('0' + i);
        }
        
        for (int i = 10; i < 36; i++) {
            symbols[i] = (char)('A' + (i - 10));
        }
    }
    
    
    public ScMeta(String deviceId, String deviceType, String appVersion)
    {
        validateDeviceId(deviceId);
        
        if ((deviceType != null) && (appVersion != null)) {
            this.deviceType = deviceType;
            this.appVersion = appVersion;            
        } else {
            ScLog.log().warning(meta(false) + String.format("Incomplete request [deviceId: %s; deviceType: %s; appVersion: %s], raising BAD_REQUEST (400).", deviceId, deviceType, appVersion));
        }
    }

    
    public String meta()
    {
        return String.format("[%s] %s/%s: ", deviceId.substring(0, 8), deviceType, appVersion);
    }
    
    
    public void validateAuthorizationHeader(String authorizationHeader)
    {
        if ((authorizationHeader != null) && (authorizationHeader.indexOf("Basic ") == 0)) {
            String base64EncodedAuthString = authorizationHeader.split(" ")[1];
            
            try {
                String authString = new String(Base64.decodeBase64(base64EncodedAuthString.getBytes("UTF-8")), "UTF-8");
                String[] authElements = authString.split(":");
                
                if (authElements.length == 2) {
                    validateUserId(authElements[0]);
                    validatePassword(authElements[1]);
                    
                    if (isValid) {
                        DAO = new ScDAO(this);
                    }
                } else {
                    ScLog.log().warning(meta(false) + String.format("Decoded authorization string '%s' is not a valid basic auth string.", authString));
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            ScLog.log().warning(meta(false) + String.format("Authorization header '%s' is not a valid basic auth header.", authorizationHeader));
        }
    }
    
    
    public void validateAuthToken(String authToken)
    {
        DAO = new ScDAO(this);
        
        try {
            ScAuthToken tokenInfo = DAO.ofy().get(ScAuthToken.class, authToken); 

            if (tokenInfo.deviceId.equals(deviceId)) {
                Date now = new Date();
                
                if (now.before(tokenInfo.dateExpires)) {
                    userId = tokenInfo.userId;
                } else {
                    ScLog.log().warning(meta(false) + String.format("Expired auth token: %s.", authToken));
                }
            } else {
                ScLog.log().warning(meta(false) + String.format("Auth token (%s) not tied to this device (%s).", authToken, deviceId));
            }
        } catch (NotFoundException e) {
            ScLog.log().warning(meta(false) + String.format("Unknown auth token: %s.", authToken));
        }
    }
    
    
    public void validateName(String name)
    {
        if ((name != null) && (name.length() > 0) && (name.indexOf(" ") > 0)) {
            this.name = name;
        } else {
            ScLog.log().warning(meta(false) + String.format("'%s' is not a full name.", name));
        }
    }
    
    
    public ScAuthInfo getAuthInfo(ScAuthPhase authPhase)
    {
        ScAuthInfo authInfo = null;
        
        if (isValid) {
            if (authPhase == ScAuthPhase.REGISTRATION) {
                registrationCode = generateRegistrationCode();
                
                authInfo = new ScAuthInfo();
                
                authInfo.userId = userId;
                authInfo.name = name;
                authInfo.deviceId = deviceId;
                authInfo.passwordHash = passwordHash;
                authInfo.registrationCode = registrationCode;
                
                try {
                    ScScolaMember member = DAO.ofy().get(ScScolaMember.class, userId);
                    
                    authInfo.isListed = true;
                    authInfo.isRegistered = member.didRegister;
                    authInfo.isAuthenticated = member.passwordHash.equals(authInfo.passwordHash);
                } catch (NotFoundException e) {
                    authInfo.isListed = false;
                    authInfo.isRegistered = false;
                    authInfo.isAuthenticated = false;
                }
            } else if (authPhase == ScAuthPhase.CONFIRMATION) {
                authInfo = DAO.get(ScAuthInfo.class, userId);
            }
        }
        
        return authInfo; 
    }
    
    
    private String meta(boolean isValid)
    {
        this.isValid = isValid;
        
        return meta();
    }
    
    
    private void validateDeviceId(String deviceId)
    {
        boolean isValid = false;
        
        if ((deviceId != null) && (deviceId.length() == 36)) {
            String[] UUIDElements = deviceId.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && (UUIDElements[0].length() ==  8);
            isValid = isValid && (UUIDElements[1].length() ==  4);
            isValid = isValid && (UUIDElements[2].length() ==  4);
            isValid = isValid && (UUIDElements[3].length() ==  4);
            isValid = isValid && (UUIDElements[4].length() == 12);
        }
        
        if (isValid) {
            this.deviceId = deviceId;
        } else {
            ScLog.log().warning(meta(false) + String.format("Device id %s is not a valid UUID.", deviceId));
        }
    }
    
    
    private void validateUserId(String userId)
    {
        if (userId != null) {
            try {
                this.emailAddress = new InternetAddress(userId);
                this.userId = userId;
            } catch (AddressException e) {
                ScLog.log().warning(meta(false) + String.format("User id %s is not a valid email address.", userId));
            }
        }
    }
    
    
    private void validatePassword(String password)
    {
        if ((password != null) && (password.length() >= kMinimumPasswordLength)) {
            this.passwordHash = ScCrypto.generatePasswordHash(password, userId);
        } else {
            if (password == null) {
                ScLog.log().warning(meta(false) + String.format("User password is null."));
            } else {
                ScLog.log().warning(meta(false) + String.format("User password is too short (minmum length: %d; actual length: %d).", kMinimumPasswordLength, password.length()));
            }
        }
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
}
