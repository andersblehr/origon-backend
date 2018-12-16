package co.origon.api.controllers;

import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import co.origon.api.OrigonApplication;
import co.origon.api.common.Dao;
import co.origon.api.common.Mailer;
import co.origon.api.entities.OAuthInfo;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.entities.OMemberProxy;
import co.origon.api.common.UrlParams;
import co.origon.api.entities.OReplicatedEntity;

import com.googlecode.objectify.Key;

import static co.origon.api.common.InputValidator.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {
    private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());
    private static final int LENGTH_ACTIVATION_CODE = 6;

    @GET
    @Path("register")
    public Response registerUser(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String userPasswordHash = checkValidPassword(credentials[1]);
        final String metadata = checkMetadata(deviceId, deviceType, appVersion);
        checkLanguage(language);
        checkNotRegistered(userEmail);

        final String activationCode = UUID.randomUUID().toString().substring(0, LENGTH_ACTIVATION_CODE);
        final OAuthInfo authInfo = new OAuthInfo(userEmail, deviceId, userPasswordHash, activationCode);
        ofy().save().entity(authInfo).now();
        new Mailer(language).sendRegistrationEmail(userEmail, activationCode);
        LOG.fine(metadata + "Sent user activation code to new user " + userEmail);

        return Response
                .status(HttpServletResponse.SC_CREATED)
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
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String userPasswordHash = checkValidPassword(credentials[1]);
        final String metadata = checkMetadata(deviceId, deviceType, appVersion);
        final OAuthInfo authInfo = checkAuthInfo(userEmail);
        checkAuthTokenFormat(authToken);
        checkNotRegistered(userEmail);

        if (authInfo.passwordHash.equals(userPasswordHash)) {
            final OAuthMeta authMeta = new OAuthMeta(authToken, userEmail, deviceId, deviceType);
            final OMemberProxy memberProxy = OMemberProxy.getOrCreate(userEmail);
            memberProxy.passwordHash = userPasswordHash;
            putAuthToken(authToken, authMeta, memberProxy);
            ofy().delete().entity(authInfo);
            LOG.fine(metadata + "Persisted new auth token for user " + userEmail);

            final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(userEmail);
            LOG.fine(metadata + "Returning " + fetchedEntities.size() + " entities");

            return Response
                    .ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
                    .header(HttpHeaders.LOCATION, memberProxy.memberId)
                    .lastModified(new Date())
                    .build();
        } else {
            throw new NotAuthorizedException(metadata + "Incorrect password");
        }
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
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String userPasswordHash = checkValidPassword(credentials[1]);
        final String metadata = checkMetadata(deviceId, deviceType, appVersion);
        final OMemberProxy memberProxy = checkRegistered(userEmail);
        checkAuthTokenFormat(authToken);

        if (memberProxy.passwordHash.equals(userPasswordHash)) {
            final OAuthMeta authMeta = new OAuthMeta(authToken, userEmail, deviceId, deviceType);
            putAuthToken(authToken, authMeta, memberProxy);
            LOG.fine(metadata + "Persisted new auth token for user " + userEmail);

            final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(userEmail, replicationDate);
            LOG.fine(metadata + "Returning " + fetchedEntities.size() + " entities");

            return Response
                    .ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
                    .header(HttpHeaders.LOCATION, memberProxy.memberId)
                    .lastModified(new Date())
                    .build();
        } else {
            throw new NotAuthorizedException(metadata + "Incorrect password");
        }
    }
    
    @GET
    @Path("change")
    public Response changePassword(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String userPasswordHash = checkValidPassword(credentials[1]);
        final OAuthMeta authMeta = checkAuthToken(authToken, userEmail);
        final String metadata = checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);
        final OMemberProxy memberProxy = checkRegistered(userEmail);

        memberProxy.passwordHash = userPasswordHash;
        ofy().save().entity(memberProxy).now();
        LOG.fine(metadata + "Saved new password hash for " + userEmail);

        return Response
                .status(HttpServletResponse.SC_CREATED)
                .build();
    }
    
    @GET
    @Path("reset")
    public Response resetPassword(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.DEVICE_ID) String deviceId,
            @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String temporaryPassword = credentials[1];
        final String temporaryPasswordHash = checkValidPassword(credentials[1]);
        final String metadata = checkMetadata(deviceId, deviceType, appVersion);
        final OMemberProxy memberProxy = checkRegistered(userEmail);
        checkLanguage(language);

        memberProxy.passwordHash = temporaryPasswordHash;
        ofy().save().entity(memberProxy).now();
        new Mailer(language).sendPasswordResetEmail(userEmail, temporaryPassword);
        LOG.fine(metadata + "Sent temporary password to " + userEmail);

        return Response
                .status(HttpServletResponse.SC_CREATED)
                .build();
    }
    
    @GET
    @Path("sendcode")
    public Response sendActivationCode(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final String[] credentials = checkBasicAuth(authorizationHeader);
        final String userEmail = checkValidEmail(credentials[0]);
        final String activationCode = credentials[1];
        final OAuthMeta authMeta = checkAuthToken(authToken, userEmail);
        final String metadata = checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);
        checkLanguage(language);
        checkRegistered(userEmail);

        final OAuthInfo authInfo = new OAuthInfo(userEmail, authMeta.deviceId, "n/a", activationCode);
        new Mailer(language).sendEmailActivationCode(userEmail, activationCode);
        LOG.fine(metadata + "Sent email activation code to " + userEmail);

        return Response
                .status(HttpServletResponse.SC_CREATED).entity(authInfo)
                .build();
    }


    private void putAuthToken(String authToken, OAuthMeta authMeta, OMemberProxy memberProxy)
    {
        Collection<OAuthMeta> authMetaItems = ofy().load().keys(memberProxy.authMetaKeys).values();

        if (authMetaItems.size() > 0) {
            for (OAuthMeta authMetaItem : authMetaItems) {
                if (authMetaItem.deviceId.equals(authMeta.deviceId)) {
                    memberProxy.authMetaKeys.remove(Key.create(OAuthMeta.class, authMetaItem.authToken));
                    ofy().delete().entity(authMetaItem);
                }
            }
        } else {
            memberProxy.didRegister = true;
        }

        memberProxy.authMetaKeys.add(Key.create(OAuthMeta.class, authToken));
        ofy().save().entities(authMeta, memberProxy).now();
    }
}
