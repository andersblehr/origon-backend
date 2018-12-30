package co.origon.api.controller;

import java.util.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.origon.api.annotation.LanguageSupported;
import co.origon.api.annotation.SessionDataValidated;
import co.origon.api.annotation.TokenAuthenticated;
import co.origon.api.common.*;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.model.ofy.entity.OAuthInfo;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.annotation.BasicAuthValidated;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@BasicAuthValidated
@SessionDataValidated
public class AuthController {
    private static final int LENGTH_ACTIVATION_CODE = 6;

    @Inject
    private DaoFactory daoFactory;

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
            @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
        final OAuthInfo authInfo = checkAwaitingActivation(basicAuthCredentials);
        checkDeviceTokenFormat(deviceToken);

        final Dao<DeviceCredentials> deviceCredentialsDao = daoFactory.daoFor(DeviceCredentials.class);
        final DeviceCredentials deviceCredentials = deviceCredentialsDao.create()
                .deviceToken(deviceToken)
                .email(basicAuthCredentials.getEmail())
                .deviceId(deviceId)
                .deviceType(deviceType);
        final Dao<MemberProxy> userProxyDao = daoFactory.daoFor(MemberProxy.class);
        final MemberProxy userProxy = userProxyDao.create()
                .proxyId(basicAuthCredentials.getEmail())
                .passwordHash(basicAuthCredentials.getPasswordHash())
                .deviceToken(deviceToken);

        deviceCredentialsDao.save(deviceCredentials);
        userProxyDao.save(userProxy);
        authInfo.delete();

        Session.log("Persisted new auth token for user " + basicAuthCredentials.getEmail());
        final List<OReplicatedEntity> fetchedEntities = ODao.getDao().fetchEntities(basicAuthCredentials.getEmail());
        Session.log("Returning " + fetchedEntities.size() + " entities");

        return Response
                .status(fetchedEntities.size() > 0 ? Status.OK : Status.NO_CONTENT)
                .entity(fetchedEntities.size() > 0 ? fetchedEntities : null)
                .header(HttpHeaders.LOCATION, userProxy.memberId())
                .lastModified(new Date())
                .build();
    }

    @GET
    @Path("login")
    public Response loginUser(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = checkAuthenticated(credentials);
        checkDeviceTokenFormat(authToken);

        userProxy.refreshDeviceToken(authToken, deviceId);
        new OAuthMeta()
                .email(credentials.getEmail())
                .authToken(authToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .save();

        Session.log("Persisted new auth token for user " + credentials.getEmail());
        final List<OReplicatedEntity> fetchedEntities = ODao.getDao().fetchEntities(credentials.getEmail(), replicationDate);
        Session.log("Returning " + fetchedEntities.size() + " entities");

        return Response
                .ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
                .header(HttpHeaders.LOCATION, userProxy.memberId())
                .lastModified(new Date())
                .build();
    }
    
    @GET
    @Path("change")
    @TokenAuthenticated
    public Response changePassword(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail());
        userProxy.passwordHash(credentials.getPasswordHash());
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
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OMemberProxy userProxy = OMemberProxy.get(credentials.getEmail());
        userProxy.passwordHash(credentials.getPasswordHash());
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
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        final String activationCode = credentials.getPassword();  // Not pretty, but how I chose to do it back then..
        final OAuthInfo authInfo = OAuthInfo.builder()
                .deviceId(authMeta.deviceId())
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
            checkArgument(!userProxy.isRegistered(), "User is already registered");
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), e, Status.CONFLICT);
        }
    }

    private static OAuthInfo checkAwaitingActivation(BasicAuthCredentials credentials) {
        try {
            checkNotRegistered(credentials.getEmail());
        } catch (WebApplicationException e) {
            throw new BadRequestException("Cannot activate an active user", e);
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
            checkArgument(userProxy.isRegistered(), "User " + credentials.getEmail() + " is not registered");
            checkArgument(userProxy.passwordHash().equals(credentials.getPasswordHash()), "Invalid password");

            return userProxy;
        } catch (IllegalArgumentException e) {
            throw new NotAuthorizedException(e.getMessage(), e);
        }
    }

    private static void checkDeviceTokenFormat(String authToken) {
        try {
            checkNotNull(authToken, "Missing parmaeter: " + UrlParams.DEVICE_TOKEN);
            checkArgument(authToken.matches("^[a-z0-9]{40}$"), "Invalid token format: " + authToken);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

}
