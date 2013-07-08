package com.origoapp.api.aux;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.origoapp.api.auth.OAuthInfo;
import com.origoapp.api.auth.OAuthMeta;
import com.origoapp.api.auth.OAuthPhase;

import static com.origoapp.api.aux.OObjectifyService.ofy;


public class OMeta
{
    private static final int kMinimumPasswordLength = 6;
    private static final int kActivationCodeLength = 6;
    
    private OAuthPhase authPhase = OAuthPhase.NONE;
    
    private ODAO DAO;
    private OMemberProxy memberProxy;
    
    private boolean isValid = true;
    
    private String email = null;
    private String userId = null;
    private String authToken = null;
    private String passwordHash = null;
    private String deviceId = null;
    private String deviceType = null;
    private String appVersion = null;
    private String activationCode = null;
    private InternetAddress emailAddress = null;
    
    
    public OMeta(String deviceId, String deviceType, String appVersion)
    {
        validateDeviceId(deviceId);
        
        if ((deviceType != null) && (appVersion != null)) {
            this.deviceType = deviceType;
            this.appVersion = appVersion;            
        } else if (deviceType == null) {
            OLog.log().warning(meta(false) + "Device type is missing.");
        } else {
            OLog.log().warning(meta(false) + "App version is missing.");
        }
    }
    
    
    public OMeta(String authToken, String appVersion)
    {
        validateAuthToken(authToken);
        
        if (appVersion != null) {
            this.appVersion = appVersion;
        } else {
            OLog.log().warning(meta(false) + "App version is missing.");
        }
    }
    
    
    public boolean isValid()
    {
        return isValid;
    }
    
    

    public String getAuthToken()
    {
        return authToken;
    }
    
    
    public String getEmail()
    {
        return email;
    }
    
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    
    public String getUserId()
    {
        return userId;
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
    
    
    public String getActivationCode()
    {
        return activationCode;
    }
    
    
    public InternetAddress getEmailAddress()
    {
        return emailAddress;
    }
    
    
    public ODAO getDAO()
    {
        if (DAO == null) {
            DAO = new ODAO(this);
        }
        
        return DAO;
    }
    
    
    public void setMemberProxy(OMemberProxy memberProxy)
    {
        this.memberProxy = memberProxy;
        this.email = memberProxy.proxyId;
    }
    
    
    public OMemberProxy getMemberProxy()
    {
        if (memberProxy == null) {
            memberProxy = ofy().load().key(Key.create(OMemberProxy.class, email)).get();
            
            if (authPhase == OAuthPhase.ACTIVATE) {
                if (memberProxy == null) {
                    memberProxy = new OMemberProxy(email);
                }
                
                memberProxy.passwordHash = passwordHash;
            }
        }
        
        return memberProxy;
    }
    
    
    public OAuthInfo getAuthInfo()
    {
        OAuthInfo authInfo = null;
        
        if (isValid) {
            if (authPhase == OAuthPhase.ACTIVATE) {
                authInfo = ofy().load().key(Key.create(OAuthInfo.class, email)).get();
            } else {
                if (authPhase == OAuthPhase.SENDCODE) {
                    authInfo = new OAuthInfo(email, deviceId, "n/a", activationCode);
                } else {
                    activationCode = deviceId.substring(0, kActivationCodeLength);
                    
                    authInfo = new OAuthInfo(email, deviceId, passwordHash, activationCode);
                    authInfo.isListed = (getMemberProxy() != null);
                }
            }
        }
        
        return authInfo; 
    }
    
    
    public void validateAuthorizationHeader(String authorizationHeader, OAuthPhase authPhase)
    {
        if ((authorizationHeader != null) && (authorizationHeader.indexOf("Basic ") == 0)) {
            String base64EncodedAuthString = authorizationHeader.split(" ")[1];
            
            try {
                String authString = new String(Base64.decodeBase64(base64EncodedAuthString.getBytes("UTF-8")), "UTF-8");
                String[] authElements = authString.split(":");
                
                if (authElements.length == 2) {
                    validateEmail(authElements[0]);
                    
                    if (isValid) {
                        this.authPhase = authPhase;
                        
                        if (authPhase == OAuthPhase.SENDCODE) {
                            this.activationCode = authElements[1];
                        } else {
                            validatePassword(authElements[1]);
                        }
                    }
                } else {
                    OLog.log().warning(meta(false) + String.format("Decoded authorization string '%s' is not a valid basic auth string.", authString));
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            OLog.log().warning(meta(false) + String.format("Authorization header '%s' is not a valid basic auth header.", authorizationHeader));
        }
    }
    
    
    public void validateAuthToken(String authToken)
    {
        if (authPhase == OAuthPhase.NONE) {
            try {
                Date now = new Date();
                OAuthMeta tokenMeta = ofy().load().type(OAuthMeta.class).id(authToken).get(); 

                if (now.before(tokenMeta.dateExpires)) {
                    this.authToken = authToken;
                    
                    email = tokenMeta.email;
                    deviceId = tokenMeta.deviceId;
                    deviceType = tokenMeta.deviceType;
                } else {
                    OLog.log().warning(meta(false) + String.format("Expired auth token: %s.", authToken));
                }
            } catch (NotFoundException e) {
                OLog.log().warning(meta(false) + String.format("Unknown auth token: %s.", authToken));
            }
        } 
    }
    
    
    public void validateTimestampToken(String URLEncodedTimestampToken)
    {
        try {
            String timestampToken = URLDecoder.decode(URLEncodedTimestampToken, "UTF-8");
            String base64EncodedTimestamp = timestampToken.substring(0, timestampToken.indexOf("=") + 1);
            String timestamp = new String(Base64.decodeBase64(base64EncodedTimestamp.getBytes("UTF-8")), "UTF-8");
            
            String seasonedAndHashedTimestamp = timestampToken.substring(timestampToken.indexOf("=") + 1);
            String reseasonedAndRehashedTimestamp = OCrypto.generateTimestampHash(timestamp);
            
            if (!reseasonedAndRehashedTimestamp.equals(seasonedAndHashedTimestamp)) {
                OLog.log().warning(meta(false) + "Timestamp does not match provided hash.");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public void validateReplicationDate(Date replicationDate)
    {
        if (replicationDate == null) {
            OLog.log().warning(meta(false) + "Replication date is missing.");
        }
    }
    
    
    public boolean isAuthenticating()
    {
        return ((authPhase == OAuthPhase.LOGIN) || (authPhase == OAuthPhase.ACTIVATE));
    }
    
    
    public String meta()
    {
        return meta(true);
    }
    
    
    private String meta(boolean isValid)
    {
        this.isValid = isValid;
        
        return String.format("[%s] %s/%s: ", deviceId, deviceType, appVersion);
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
            OLog.log().warning(meta(false) + String.format("Device id %s is not a valid UUID.", deviceId));
        }
    }
    
    
    private void validateEmail(String email)
    {
        if (email != null) {
            try {
                this.emailAddress = new InternetAddress(email);
                this.email = email;
            } catch (AddressException e) {
                OLog.log().warning(meta(false) + String.format("'%s' is not a valid email address.", email));
            }
        }
    }
    
    
    private void validatePassword(String password)
    {
        if ((password != null) && (password.length() >= kMinimumPasswordLength)) {
            this.passwordHash = OCrypto.generatePasswordHash(password);
        } else {
            if (password == null) {
                OLog.log().warning(meta(false) + String.format("User password is null."));
            } else {
                OLog.log().warning(meta(false) + String.format("User password is too short (minmum length: %d; actual length: %d).", kMinimumPasswordLength, password.length()));
            }
        }
    }
}
