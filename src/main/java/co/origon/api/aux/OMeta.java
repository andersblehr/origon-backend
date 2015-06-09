package co.origon.api.aux;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.Base64;

import co.origon.api.auth.OAuthInfo;
import co.origon.api.auth.OAuthMeta;
import co.origon.api.auth.OAuthPhase;

import com.googlecode.objectify.Key;

import static co.origon.api.aux.OObjectifyService.ofy;


public class OMeta
{
    private static final int kMinimumPasswordLength = 6;
    private static final int kActivationCodeLength = 6;
    
    private OAuthPhase authPhase = OAuthPhase.NONE;
    
    private ODAO DAO;
    private OMemberProxy memberProxy;
    
    private boolean isValid = true;
    
    private String email = null;
    private String authToken = null;
    private String passwordHash = null;
    private String deviceId = null;
    private String deviceType = null;
    private String appVersion = null;
    private String language = null;
    private String activationCode = null;
    
    
    public OMeta(String deviceId, String deviceType, String appVersion, String language)
    {
        validateDeviceId(deviceId);
        
        if (deviceType != null && appVersion != null) {
            this.deviceType = deviceType;
            this.appVersion = appVersion;
            this.language = language;
        } else if (deviceType == null) {
            OLog.log().warning(meta(false) + "Device type is missing.");
        } else {
            OLog.log().warning(meta(false) + "App version is missing.");
        }
    }
    
    
    public OMeta(String authToken, String appVersion, String language)
    {
        validateAuthToken(authToken);
        
        if (appVersion != null) {
            this.appVersion = appVersion;
            this.language = language;
        } else {
            OLog.log().warning(meta(false) + "App version is missing.");
        }
    }
    
    
    public boolean isDownForMaintenance()
    {
        return false;
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
    
    
    public String getAppVersion()
    {
        return appVersion;
    }
    
    
    public String getLanguage()
    {
        return language;
    }
    
    
    public String getActivationCode()
    {
        return activationCode;
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
            memberProxy = ofy().load().key(Key.create(OMemberProxy.class, email)).now();
            
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
                authInfo = ofy().load().key(Key.create(OAuthInfo.class, email)).now();
            } else {
                if (authPhase == OAuthPhase.SENDCODE) {
                    authInfo = new OAuthInfo(email, deviceId, "n/a", activationCode);
                } else {
                    activationCode = UUID.randomUUID().toString().substring(0, kActivationCodeLength);
                    
                    authInfo = new OAuthInfo(email, deviceId, passwordHash, activationCode);
                }
            }
        }
        
        return authInfo; 
    }
    
    
    public void validateAuthorizationHeader(String authorizationHeader, OAuthPhase authPhase)
    {
        if (authorizationHeader != null && authorizationHeader.indexOf("Basic ") == 0) {
            String base64EncodedAuthString = authorizationHeader.split(" ")[1];
            
            try {
                String authString = new String(Base64.decodeBase64(base64EncodedAuthString.getBytes("UTF-8")), "UTF-8");
                String[] authElements = authString.split(":");
                
                if (authElements.length == 2) {
                    validateEmail(authElements[0]);
                    
                    if (isValid) {
                        this.authPhase = authPhase;
                        
                        if (authPhase == OAuthPhase.CHANGE || authPhase == OAuthPhase.RESET) {
                            this.passwordHash = OCrypto.generatePasswordHash(authElements[1]);
                            
                            if (authPhase == OAuthPhase.RESET) {
                                this.activationCode = authElements[1];
                            }
                        } else if (authPhase == OAuthPhase.SENDCODE) {
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
            Date now = new Date();
            OAuthMeta tokenMeta = ofy().load().type(OAuthMeta.class).id(authToken).now(); 

            if (tokenMeta != null) {
                if (now.before(tokenMeta.dateExpires)) {
                    this.authToken = authToken;
                    
                    email = tokenMeta.email;
                    deviceId = tokenMeta.deviceId;
                    deviceType = tokenMeta.deviceType;
                } else {
                    OLog.log().warning(meta(false) + String.format("Expired auth token: %s.", authToken));
                }
            } else {
                OLog.log().warning(meta(false) + String.format("Unknown auth token: %s.", authToken));
            }
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
        return authPhase == OAuthPhase.LOGIN || authPhase == OAuthPhase.ACTIVATE;
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
        
        if (UUID != null && UUID.length() == 36) {
            String[] UUIDElements = UUID.split("-");
            
            isValid = (UUIDElements.length == 5);
            isValid = isValid && UUIDElements[0].length() ==  8;
            isValid = isValid && UUIDElements[1].length() ==  4;
            isValid = isValid && UUIDElements[2].length() ==  4;
            isValid = isValid && UUIDElements[3].length() ==  4;
            isValid = isValid && UUIDElements[4].length() == 12;
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
                new InternetAddress(email);
                
                this.email = email;
            } catch (AddressException e) {
                OLog.log().warning(meta(false) + String.format("'%s' is not a valid email address.", email));
            }
        }
    }
    
    
    private void validatePassword(String password)
    {
        if (password != null && password.length() >= kMinimumPasswordLength) {
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
