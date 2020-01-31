package co.origon.api.controller;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Mailer;
import co.origon.api.common.Matcher;
import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.filter.SupportedLanguage;
import co.origon.api.filter.ValidBasicAuthCredentials;
import co.origon.api.filter.ValidDeviceToken;
import co.origon.api.filter.ValidSessionData;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.model.api.entity.OtpCredentials;
import co.origon.api.model.api.entity.ReplicatedEntity;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Singleton
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@ValidBasicAuthCredentials
@ValidSessionData
public class AuthController {

  public static final String WWW_AUTH_CHALLENGE_BASIC_AUTH = "login";

  private static final int LENGTH_ACTIVATION_CODE = 6;

  @Inject private AuthService authService;
  @Inject private ReplicationService replicationService;
  @Inject private DaoFactory daoFactory;
  @Inject private Mailer mailer;

  @GET
  @Path("register")
  @SupportedLanguage
  public Response registerUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion,
      @QueryParam(UrlParams.LANGUAGE) String language) {
    final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
    checkNotRegistered(credentials.email());

    final Dao<OtpCredentials> dao = daoFactory.daoFor(OtpCredentials.class);
    final OtpCredentials otpCredentials =
        dao.create()
            .deviceId(deviceId)
            .email(credentials.email())
            .passwordHash(credentials.passwordHash())
            .activationCode(UUID.randomUUID().toString().substring(0, LENGTH_ACTIVATION_CODE));
    dao.save(otpCredentials);

    mailer
        .using(language)
        .sendRegistrationEmail(credentials.email(), otpCredentials.activationCode());
    Session.log("Sent user activation code to new user " + credentials.email());

    return Response.status(Status.CREATED).entity(otpCredentials).build();
  }

  @GET
  @Path("activate")
  public Response activateUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
    final OtpCredentials otpCredentials = checkAwaitingActivation(basicAuthCredentials);
    checkDeviceTokenFormat(deviceToken);

    final Dao<DeviceCredentials> deviceCredentialsDao = daoFactory.daoFor(DeviceCredentials.class);
    final DeviceCredentials deviceCredentials =
        deviceCredentialsDao
            .create()
            .deviceToken(deviceToken)
            .email(basicAuthCredentials.email())
            .deviceId(deviceId)
            .deviceType(deviceType);
    final Dao<MemberProxy> userProxyDao = daoFactory.daoFor(MemberProxy.class);
    final MemberProxy userProxy =
        userProxyDao
            .create() // TODO
            // .produce(basicAuthCredentials.email())
            .passwordHash(basicAuthCredentials.passwordHash())
            .deviceToken(deviceToken);

    deviceCredentialsDao.save(deviceCredentials);
    userProxyDao.save(userProxy);
    daoFactory.daoFor(OtpCredentials.class).delete(otpCredentials);

    Session.log("Persisted new device token for user " + basicAuthCredentials.email());
    final List<ReplicatedEntity> fetchedEntities =
        replicationService.fetch(basicAuthCredentials.email());
    Session.log("Returning " + fetchedEntities.size() + " entities");

    return Response.status(fetchedEntities.size() > 0 ? Status.OK : Status.NO_CONTENT)
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
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
    final MemberProxy userProxy = checkAuthenticated(credentials);
    checkDeviceTokenFormat(deviceToken);

    final Dao<DeviceCredentials> dao = daoFactory.daoFor(DeviceCredentials.class);
    final DeviceCredentials deviceCredentials =
        dao.create()
            .email(credentials.email())
            .deviceToken(deviceToken)
            .deviceId(deviceId)
            .deviceType(deviceType);
    dao.save(deviceCredentials);
    authService.refreshDeviceToken(userProxy, deviceId, deviceToken);

    Session.log("Persisted new device token for user " + credentials.email());
    final List<ReplicatedEntity> fetchedEntities =
        replicationService.fetch(credentials.email(), replicationDate);
    Session.log("Returning " + fetchedEntities.size() + " entities");

    return Response.ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
        .header(HttpHeaders.LOCATION, userProxy.memberId())
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("change")
  @ValidDeviceToken
  public Response changePassword(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
    final Dao<MemberProxy> dao = daoFactory.daoFor(MemberProxy.class);
    final MemberProxy userProxy =
        dao.get(credentials.email()).passwordHash(credentials.passwordHash());
    dao.save(userProxy);

    Session.log("Saved new password hash for " + credentials.email());

    return Response.status(Status.CREATED).build();
  }

  @GET
  @Path("reset")
  @ValidDeviceToken
  @SupportedLanguage
  public Response resetPassword(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion,
      @QueryParam(UrlParams.LANGUAGE) String language) {
    final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
    final Dao<MemberProxy> dao = daoFactory.daoFor(MemberProxy.class);
    final MemberProxy userProxy =
        dao.get(credentials.email()).passwordHash(credentials.passwordHash());
    dao.save(userProxy);

    mailer.using(language).sendPasswordResetEmail(credentials.email(), credentials.password());
    Session.log("Sent temporary password to " + credentials.email());

    return Response.status(Status.CREATED).build();
  }

  @GET
  @Path("sendcode")
  @ValidDeviceToken
  @SupportedLanguage
  public Response sendActivationCode(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion,
      @QueryParam(UrlParams.LANGUAGE) String language) {
    final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
    final DeviceCredentials deviceCredentials =
        daoFactory.daoFor(DeviceCredentials.class).get(deviceToken);
    final String activationCode = basicAuthCredentials.password(); // Not pretty...
    final Dao<OtpCredentials> dao = daoFactory.daoFor(OtpCredentials.class);
    final OtpCredentials otpCredentials =
        dao.create()
            .deviceId(deviceCredentials.deviceId())
            .email(basicAuthCredentials.email())
            .activationCode(activationCode);
    dao.save(otpCredentials);

    mailer.using(language).sendEmailActivationCode(basicAuthCredentials.email(), activationCode);
    Session.log("Sent email activation code to " + basicAuthCredentials.email());

    return Response.status(Status.CREATED).entity(otpCredentials).build();
  }

  private void checkNotRegistered(String email) {
    final MemberProxy userProxy = daoFactory.daoFor(MemberProxy.class).get(email);
    if (userProxy != null && userProxy.isRegistered())
      throw new WebApplicationException("User is already registered", Status.CONFLICT);
  }

  private OtpCredentials checkAwaitingActivation(BasicAuthCredentials credentials) {
    final MemberProxy userProxy = daoFactory.daoFor(MemberProxy.class).get(credentials.email());
    if (userProxy != null && userProxy.isRegistered())
      throw new BadRequestException("Cannot activate an active user");

    final OtpCredentials otpCredentials =
        daoFactory.daoFor(OtpCredentials.class).get(credentials.email());
    if (otpCredentials == null)
      throw new BadRequestException("User is not awaiting activation, cannot activate");
    if (!otpCredentials.passwordHash().equals(credentials.passwordHash()))
      throw new NotAuthorizedException("Incorrect password", WWW_AUTH_CHALLENGE_BASIC_AUTH);

    return otpCredentials;
  }

  private MemberProxy checkAuthenticated(BasicAuthCredentials credentials) {
    final MemberProxy userProxy = daoFactory.daoFor(MemberProxy.class).get(credentials.email());
    if (!userProxy.isRegistered())
      throw new BadRequestException("User is not registered, cannot authenticate");
    if (!userProxy.passwordHash().equals(credentials.passwordHash()))
      throw new NotAuthorizedException("Invalid password", WWW_AUTH_CHALLENGE_BASIC_AUTH);

    return userProxy;
  }

  private void checkDeviceTokenFormat(String deviceToken) {
    if (deviceToken == null || deviceToken.length() == 0)
      throw new BadRequestException("Missing parameter: " + UrlParams.DEVICE_TOKEN);
    if (!Matcher.isDeviceToken(deviceToken))
      throw new BadRequestException("Invalid device token format: " + deviceToken);
  }
}
