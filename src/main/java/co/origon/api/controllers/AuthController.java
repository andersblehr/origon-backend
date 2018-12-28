package co.origon.api.controllers;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.origon.api.annotations.LanguageSupported;
import co.origon.api.annotations.SessionDataValidated;
import co.origon.api.annotations.TokenAuthenticated;
import co.origon.api.common.*;
import co.origon.api.entities.OAuthInfo;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.entities.OMemberProxy;
import co.origon.api.entities.OReplicatedEntity;
import co.origon.api.annotations.BasicAuthValidated;

import com.googlecode.objectify.Key;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@BasicAuthValidated
@SessionDataValidated
public class AuthController {
    private static final int LENGTH_ACTIVATION_CODE = 6;

    @GET
    @Path("register")
    @LanguageSupported
    public Response registerUser(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        checkNotRegistered(credentials.getEmail());

        final OAuthInfo authInfo = OAuthInfo.builder()
                .deviceId(deviceId)
                .email(credentials.getEmail())
                .passwordHash(credentials.getPasswordHash())
                .activationCode(UUID.randomUUID().toString().substring(0, LENGTH_ACTIVATION_CODE))
                .build();
        authInfo.save();

        Mailer.forLanguage(language).sendRegistrationEmail(credentials.getEmail(), authInfo.getActivationCode());
        Session.log("Sent user activation code to new user " + credentials.getEmail());

        return Response
                .status(Status.CREATED)
                .entity(authInfo)
                .build();
    }

    @GET
    @Path("activate")
    public Response activateUser(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OAuthInfo authInfo = checkAwaitingActivation(credentials);
        checkAuthTokenFormat(authToken);

        final OAuthMeta authMeta = OAuthMeta.builder()
                .authToken(authToken)
                .email(credentials.getEmail())
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build();
        final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail()).toBuilder()
                .proxyId(credentials.getEmail())
                .passwordHash(credentials.getPasswordHash())
                .authMetaKey(Key.create(OAuthMeta.class, authToken))
                .build();

        authMeta.save();
        userProxy.save();
        authInfo.delete();

        Session.log("Persisted new auth token for user " + credentials.getEmail());
        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(credentials.getEmail());
        Session.log("Returning " + fetchedEntities.size() + " entities");

        return Response
                .status(fetchedEntities.size() > 0 ? Status.OK : Status.NO_CONTENT)
                .entity(fetchedEntities.size() > 0 ? fetchedEntities : null)
                .header(HttpHeaders.LOCATION, userProxy.getMemberId())
                .lastModified(new Date())
                .build();
    }

    @GET
    @Path("login")
    public Response loginUser(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = checkAuthenticated(credentials);
        checkAuthTokenFormat(authToken);

        userProxy.refreshAuthTokenForDevice(authToken, deviceId);
        OAuthMeta.builder()
                .email(credentials.getEmail())
                .authToken(authToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build()
                .save();

        Session.log("Persisted new auth token for user " + credentials.getEmail());
        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(credentials.getEmail(), replicationDate);
        Session.log("Returning " + fetchedEntities.size() + " entities");

        return Response
                .ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
                .header(HttpHeaders.LOCATION, userProxy.getMemberId())
                .lastModified(new Date())
                .build();
    }
    
    @GET
    @Path("change")
    @TokenAuthenticated
    public Response changePassword(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail());
        userProxy.setPasswordHash(credentials.getPasswordHash());
        userProxy.save();

        Session.log("Saved new password hash for " + credentials.getEmail());

        return Response
                .status(Status.CREATED)
                .build();
    }
    
    @GET
    @Path("reset")
    @TokenAuthenticated
    @LanguageSupported
    public Response resetPassword(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail());
        userProxy.setPasswordHash(credentials.getPasswordHash());
        userProxy.save();

        Mailer.forLanguage(language).sendPasswordResetEmail(credentials.getEmail(), credentials.getPassword());
        Session.log("Sent temporary password to " + credentials.getEmail());

        return Response
                .status(Status.CREATED)
                .build();
    }
    
    @GET
    @Path("sendcode")
    @TokenAuthenticated
    @LanguageSupported
    public Response sendActivationCode(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        final String activationCode = credentials.getPassword();  // Not pretty, but how I chose to do it back then..
        final OAuthInfo authInfo = OAuthInfo.builder()
                .deviceId(authMeta.getDeviceId())
                .email(credentials.getEmail())
                .activationCode(activationCode)
                .build();
        authInfo.save();

        Mailer.forLanguage(language).sendEmailActivationCode(credentials.getEmail(), activationCode);
        Session.log("Sent email activation code to " + credentials.getEmail());

        return Response
                .status(Status.CREATED)
                .entity(authInfo)
                .build();
    }

    private static void checkNotRegistered(String email) {
        try {
            final OMemberProxy userProxy = OMemberProxy.get(email);
            checkArgument(!userProxy.didRegister(), "User is already registered");
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), e, Status.CONFLICT);
        }
    }

    private static OAuthInfo checkAwaitingActivation(BasicAuthCredentials credentials) {
        try {
            checkNotRegistered(credentials.getEmail());
        } catch (WebApplicationException e) {
            throw new ForbiddenException("Cannot activate an active user", e);
        }

        final OAuthInfo authInfo = OAuthInfo.get(credentials.getEmail());
        if (authInfo == null) {
            throw new BadRequestException("User " + credentials.getEmail() + " is not awaiting activation, cannot activate");
        }
        if (!authInfo.getPasswordHash().equals(credentials.getPasswordHash())) {
            throw new NotAuthorizedException("Incorrect password");
        }

        return authInfo;
    }

    private static OMemberProxy checkAuthenticated(BasicAuthCredentials credentials) {
        try {
            final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail());
            checkArgument(userProxy.didRegister(), "User " + credentials.getEmail() + " is not registered");
            checkArgument(userProxy.getPasswordHash().equals(credentials.getPasswordHash()), "Invalid password");

            return userProxy;
        } catch (IllegalArgumentException e) {
            throw new NotAuthorizedException(e.getMessage(), e);
        }
    }

    private static void checkAuthTokenFormat(String authToken) {
        try {
            checkNotNull(authToken, "Missing parmaeter: " + UrlParams.AUTH_TOKEN);
            checkArgument(authToken.matches("^[a-z0-9]{40}$"), "Invalid token format: " + authToken);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

}
