package com.scolaapp.api.aux;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.scolaapp.api.auth.ScAuthInfo;
import com.scolaapp.api.auth.ScAuthPhase;
import com.scolaapp.api.auth.ScAuthTokenMeta;
import com.scolaapp.api.model.ScMember;
import com.scolaapp.api.model.ScScola;
import com.scolaapp.api.model.proxy.ScMemberProxy;


public class ScMeta
{
    private static final char[] symbols = new char[16];
    
    private static final int kMinimumPasswordLength = 6;
    private static final int kRegistrationCodeLength = 6;
    
    private ScAuthPhase authPhase = ScAuthPhase.NONE;
    
    private ScDAO DAO;
    private ScMemberProxy memberProxy;
    
    private boolean isValid = true;
    
    private String name = null;
    private String userId = null;
    private String scolaId = null;
    private String authToken = null;
    private String passwordHash = null;
    private String deviceId = null;
    private String deviceType = null;
    private String appVersion = null;
    private String registrationCode = null;

    private InternetAddress emailAddress = null;
    
    
    static
    {
        for (int i = 0; i < 10; i++) {
            symbols[i] = (char)('0' + i);
        }
        
        for (int i = 10; i < 16; i++) {
            symbols[i] = (char)('a' + (i - 10));
        }
    }
    
    
    public ScMeta(String deviceId, String deviceType, String appVersion)
    {
        validateDeviceId(deviceId);
        
        if ((deviceType != null) && (appVersion != null)) {
            this.deviceType = deviceType;
            this.appVersion = appVersion;            
        } else if (deviceType == null) {
            ScLog.log().warning(meta(false) + "Device type is missing.");
        } else {
            ScLog.log().warning(meta(false) + "App version is missing.");
        }
    }
    
    
    public ScMeta(String scolaId, String deviceId, String deviceType, String appVersion)
    {
        this(deviceId, deviceType, appVersion);
        
        validateScolaId(scolaId);
    }
    
    
    public ScMeta(String authToken, String appVersion)
    {
        validateAuthToken(authToken);
        
        if (isValid) {
            this.appVersion = appVersion;
        }
    }
    
    
    public boolean isValid()
    {
        return isValid;
    }

    
    public String getUserId()
    {
        return userId;
    }
    
    
    public String getScolaId()
    {
        return scolaId;
    }
    
    
    public String getAuthToken()
    {
        return authToken;
    }
    
    
    public String getPasswordHash()
    {
        return passwordHash;
    }
    
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    
    public String getDeviceType()
    {
        return deviceType;
    }
    
    
    public String getRegistrationCode()
    {
        return registrationCode;
    }
    
    
    public InternetAddress getEmailAddress()
    {
        return emailAddress;
    }
    
    
    public ScDAO getDAO()
    {
        return DAO;
    }
    
    
    public Key<ScScola> getScolaKey()
    {
        return new Key<ScScola>(ScScola.class, scolaId);
    }
    
    
    public ScMemberProxy getMemberProxy()
    {
        if (memberProxy == null) {
            if (authPhase == ScAuthPhase.CONFIRMATION) {
                memberProxy = new ScMemberProxy(userId, scolaId);
            } else {
                memberProxy = DAO.get(new Key<ScMemberProxy>(ScMemberProxy.class, userId));
            }
        }
        
        return memberProxy;
    }
    
    
    public ScAuthInfo getAuthInfo()
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
                
                ScMemberProxy memberProxy = getMemberProxy();
                
                if (memberProxy != null) {
                    ScMember member = DAO.get(memberProxy.memberKey);
                    
                    authInfo.isListed = true;
                    authInfo.isRegistered = member.didRegister;
                    authInfo.isAuthenticated = member.passwordHash.equals(authInfo.passwordHash);
                    authInfo.homeScolaId = member.scolaId;
                } else {
                    authInfo.isListed = false;
                    authInfo.isRegistered = false;
                    authInfo.isAuthenticated = false;
                }
            } else if (authPhase == ScAuthPhase.CONFIRMATION) {
                authInfo = DAO.get(new Key<ScAuthInfo>(ScAuthInfo.class, userId));
            }
        }
        
        return authInfo; 
    }
    
    
    public void validateAuthorizationHeader(String authorizationHeader, ScAuthPhase authPhase)
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
                        this.authPhase = authPhase;
                        this.DAO = new ScDAO(this);
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
            Date now = new Date();
            ScAuthTokenMeta tokenMeta = DAO.ofy().get(ScAuthTokenMeta.class, authToken); 

            if (now.before(tokenMeta.dateExpires)) {
                this.authToken = authToken;
                
                userId = tokenMeta.userId;
                scolaId = tokenMeta.scolaId;
                deviceId = tokenMeta.deviceId;
                deviceType = tokenMeta.deviceType;
            } else {
                ScLog.log().warning(meta(false) + String.format("Expired auth token: %s.", authToken));
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
    
    
    public String meta()
    {
        return String.format("[%s] %s/%s: ", deviceId, deviceType, appVersion);
    }
    
    
    private String meta(boolean isValid)
    {
        this.isValid = isValid;
        
        return meta();
    }
    
    
    private boolean isValidUUID(String UUID)
    {
        boolean isValid = false;
        
        if ((UUID != null) && (UUID.length() == 36)) {
            String[] UUIDElements = UUID.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && (UUIDElements[0].length() ==  8);
            isValid = isValid && (UUIDElements[1].length() ==  4);
            isValid = isValid && (UUIDElements[2].length() ==  4);
            isValid = isValid && (UUIDElements[3].length() ==  4);
            isValid = isValid && (UUIDElements[4].length() == 12);
        }

        return isValid;
    }
    
    
    private void validateDeviceId(String deviceId)
    {
        if (isValidUUID(deviceId)) {
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
    
    
    private void validateScolaId(String scolaId)
    {
        if (isValidUUID(scolaId)) {
            this.scolaId = scolaId;
        } else {
            ScLog.log().warning(meta(false) + String.format("User home scola id %s is not a valid UUID.", scolaId));
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
