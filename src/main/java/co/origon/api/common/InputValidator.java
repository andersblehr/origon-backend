package co.origon.api.common;

import co.origon.api.entities.OAuthInfo;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.entities.OMemberProxy;
import co.origon.api.exceptions.IllegalCredentialsException;
import co.origon.api.exceptions.InvalidInputException;
import com.googlecode.objectify.Key;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class InputValidator {
    private static final int LENGTH_PASSWORD_MIN = 6;

    public static String[] checkBasicAuth(String authHeader) {
        try {
            checkNotNull(authHeader, "Missing HTTP header: " + HttpHeaders.AUTHORIZATION);
            final String[] authElements = authHeader.split(" ");
            checkArgument(authElements.length == 2, "Invalid AUTHORIZATION header: " + authHeader);
            checkArgument(authElements[0].equals("Basic"), "Invalid authorization scheme: " + authElements[0]);
            final String[] credentials = (new String(Base64.getDecoder().decode(authElements[1].getBytes()))).split(":");
            checkArgument(credentials.length == 2, "Invalid Basic auth credentials: " + authElements[1]);
            checkArgument(credentials[0].length() > 0, "First basic auth credential has length 0");
            checkArgument(credentials[1].length() > 0, "Second basic auth credential has length 0");

            return credentials;
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static String checkValidEmail(String email) {
        try {
            checkNotNull(email, "Email address is null");

            return (new InternetAddress(email)).getAddress();
        } catch (AddressException e) {
            throw new InvalidInputException("Invalid email address: " + email, e);
        }
    }

    public static String checkValidPassword(String password) {
        try {
            checkNotNull(password, "Password is null");
            checkArgument(password.length() >= LENGTH_PASSWORD_MIN, "Password is too short");

            return Crypto.generatePasswordHash(password);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static void checkReplicationDate(Date replicationDate) {
        try {
            checkNotNull(replicationDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
            checkArgument(replicationDate.before(new Date()), "Invalid last replication date: " + replicationDate);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static String checkMetadata(String deviceId, String deviceType, String appVersion) {
        try {
            return String.format("[%s] %s/%s: ",
                    checkNotNull(deviceId, "Missing parameter: " + UrlParams.DEVICE_ID),
                    checkNotNull(deviceType, "Missing parameter: " + UrlParams.DEVICE_TYPE),
                    checkNotNull(appVersion, "Missing parameter: " + UrlParams.APP_VERSION)
            );
        } catch (NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static void checkLanguage(String language) {
        try {
            checkNotNull(language, "Missing parameter: " + UrlParams.LANGUAGE);
            checkArgument(Arrays.asList(new String[]{"no", "en", "de"}).contains(language), "Illegal language: " + language);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static void checkAuthTokenFormat(String authToken) {
        try {
            checkNotNull(authToken, "Missing parmaeter: " + UrlParams.AUTH_TOKEN);
            checkArgument(authToken.matches("^[a-z0-9]{40}$"), "Invalid token format: " + authToken);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidInputException(e);
        }
    }

    public static OAuthMeta checkAuthToken(String authToken) {
        try {
            checkAuthTokenFormat(authToken);
            final OAuthMeta authMeta = ofy().load().type(OAuthMeta.class).id(authToken).now();
            checkNotNull(authMeta, "Unknown authentication token: " + authToken);

            return authMeta;
        } catch (NullPointerException e) {
            throw new IllegalCredentialsException(e);
        }
    }

    public static OAuthMeta checkAuthToken(String authToken, String email) {
        try {
            checkAuthTokenFormat(authToken);
            checkValidEmail(email);
            final OAuthMeta authMeta = checkAuthToken(authToken);
            checkArgument(authMeta.email.equals(email), "Email address does not match records");

            return authMeta;
        } catch (IllegalArgumentException e) {
            throw new IllegalCredentialsException(e);
        }
    }

    public static OAuthInfo checkAuthInfo(String email) {
        try {
            checkValidEmail(email);
            final OAuthInfo authInfo = ofy().load().key(Key.create(OAuthInfo.class, email)).now();
            checkNotNull(authInfo, "User " + email + " is not pending activation, cannot activate");

            return authInfo;
        } catch (NullPointerException e) {
            throw new IllegalCredentialsException(e);
        }
    }

    public static void checkNotRegistered(String email) {
        try {
            checkValidEmail(email);
            final OMemberProxy memberProxy = OMemberProxy.get(email);
            checkArgument(memberProxy == null || !memberProxy.didRegister, "User " + email + " is already registered");
        } catch (IllegalArgumentException e) {
            throw new IllegalCredentialsException(e);
        }
    }

    public static OMemberProxy checkRegistered(String email) {
        try {
            checkValidEmail(email);
            final OMemberProxy memberProxy = OMemberProxy.get(email);
            checkArgument(memberProxy != null && memberProxy.didRegister, "User " + email + " is not registered");

            return memberProxy;
        } catch (IllegalArgumentException e) {
            throw new IllegalCredentialsException(e);
        }
    }
}
