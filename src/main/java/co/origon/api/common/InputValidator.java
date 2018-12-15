package co.origon.api.common;

import co.origon.api.entities.OAuthInfo;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.entities.OMemberProxy;
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
        checkNotNull(authHeader, "Missing HTTP header: " + HttpHeaders.AUTHORIZATION);
        final String[] authElements = authHeader.split(" ");
        checkArgument(authElements.length == 2, "Invalid AUTHORIZATION header: " + authHeader);
        checkArgument(authElements[0].equals("Basic"), "Invalid authorization scheme: " + authElements[0]);
        final String[] credentials = (new String(Base64.getDecoder().decode(authElements[1].getBytes()))).split(":");
        checkArgument(credentials.length == 2, "Invalid Basic auth credentials: " + authElements[1]);
        checkArgument(credentials[0].length() > 0, "First basic auth credential has length 0");
        checkArgument(credentials[1].length() > 0, "Second basic auth credential has length 0");

        return credentials;
    }

    public static String checkValidEmail(String userEmail) {
        try {
            new InternetAddress(userEmail);
        } catch (AddressException e) {
            throw new IllegalArgumentException("Illegal email address: " + userEmail);
        }

        return userEmail;
    }

    public static String checkValidPassword(String userPassword) {
        if (userPassword.length() < LENGTH_PASSWORD_MIN) {
            throw new IllegalArgumentException("Password is too short, must have " + LENGTH_PASSWORD_MIN + " characters or more" );
        }

        return Crypto.generatePasswordHash(userPassword);
    }

    public static String checkMetadata(String deviceId, String deviceType, String appVersion) {
        return String.format("[%s] %s/%s: ",
                checkNotNull(deviceId, "Missing parameter: " + UrlParams.DEVICE_ID),
                checkNotNull(deviceType, "Missing parameter: " + UrlParams.DEVICE_TYPE),
                checkNotNull(appVersion, "Missing parameter: " + UrlParams.APP_VERSION)
        );
    }

    public static void checkLanguage(String language) {
        checkNotNull(language, "Missing parameter: " + UrlParams.LANGUAGE);
        checkArgument(Arrays.asList(new String[]{"no", "en", "de"}).contains(language), "Illegal language: " + language);
    }

    public static void checkAuthTokenFormat(String authToken) {
        checkNotNull(authToken, "Missing parmaeter: " + UrlParams.AUTH_TOKEN);
        checkArgument(authToken.length() == 40, "Invalid token format: " + authToken);
        checkArgument(authToken.matches("[a-z0-9]"), "Invalid token format: " + authToken);
    }

    public static OAuthMeta checkAuthToken(String authToken) {
        checkNotNull(authToken, "Missing parameter: " + UrlParams.AUTH_TOKEN);

        return ofy().load().type(OAuthMeta.class).id(authToken).now();
    }

    public static OAuthMeta checkAuthToken(String authToken, String email) {
        final OAuthMeta authMeta = checkAuthToken(authToken);
        checkArgument(authMeta.email.equals(email), "Email address does not match records");

        return authMeta;
    }

    public static OAuthInfo checkAuthInfo(String email) {
        final OAuthInfo authInfo = ofy().load().key(Key.create(OAuthInfo.class, email)).now();
        checkNotNull(authInfo, "User " + email + "  has not received activation code, cannot activate");

        return authInfo;
    }

    public static void checkNotRegistered(String email) {
        final OMemberProxy memberProxy = OMemberProxy.get(email);
        checkArgument(memberProxy == null || !memberProxy.didRegister, "Email " + email + " already registered");
    }

    public static OMemberProxy checkRegistered(String email) {
        final OMemberProxy memberProxy = OMemberProxy.get(email);
        checkArgument(memberProxy != null && memberProxy.didRegister, "Email " + email + " is not registered");

        return memberProxy;
    }

    public static void checkReplicationDate(Date replictionDate) {
        checkNotNull(replictionDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
        checkArgument(replictionDate.before(new Date()), "Invalid last replication date: " + replictionDate);
    }
}
