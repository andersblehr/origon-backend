package co.origon.api.controller;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.UrlParams;
import co.origon.api.filter.SupportedLanguage;
import co.origon.api.filter.ValidBasicAuthCredentials;
import co.origon.api.filter.ValidDeviceToken;
import co.origon.api.filter.ValidSessionData;
import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@ValidBasicAuthCredentials
@ValidSessionData
public class AuthController {

  public static final String WWW_AUTH_CHALLENGE_BASIC_AUTH = "login";

  private AuthService authService;
  private ReplicationService replicationService;

  @Inject
  AuthController(AuthService authService, ReplicationService replicationService) {
    this.authService = authService;
    this.replicationService = replicationService;
  }

  @GET
  @Path("register")
  @SupportedLanguage
  public Response registerUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion,
      @QueryParam(UrlParams.LANGUAGE) String language) {

    return Response.status(Status.CREATED)
        .entity(
            authService.registerUser(
                BasicAuthCredentials.getCredentials(), deviceId, Language.fromCode(language)))
        .build();
  }

  @GET
  @Path("activate")
  public Response activateUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {

    checkDeviceTokenFormat(deviceToken);
    final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
    final MemberProxy userProxy =
        authService.activateUser(
            DeviceCredentials.builder()
                .email(basicAuthCredentials.email())
                .authToken(deviceToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build(),
            basicAuthCredentials.passwordHash());
    final List<ReplicatedEntity> entityGraph =
        replicationService.fetch(basicAuthCredentials.email());

    return Response.status(entityGraph.size() > 0 ? Status.OK : Status.NO_CONTENT)
        .entity(entityGraph.size() > 0 ? entityGraph : null)
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

    checkDeviceTokenFormat(deviceToken);
    final BasicAuthCredentials basicAuthCredentials = BasicAuthCredentials.getCredentials();
    final MemberProxy userProxy =
        authService.loginUser(
            DeviceCredentials.builder()
                .email(basicAuthCredentials.email())
                .authToken(deviceToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build(),
            basicAuthCredentials.passwordHash());
    final List<ReplicatedEntity> entityGraph =
        replicationService.fetch(basicAuthCredentials.email(), replicationDate);

    return Response.ok(entityGraph.size() > 0 ? entityGraph : null)
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
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {

    authService.changePassword(BasicAuthCredentials.getCredentials());

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

    authService.resetPassword(BasicAuthCredentials.getCredentials(), Language.fromCode(language));

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

    return Response.status(Status.CREATED)
        .entity(
            authService.sendActivationCode(
                BasicAuthCredentials.getCredentials(), deviceToken, Language.fromCode(language)))
        .build();
  }

  private void checkDeviceTokenFormat(String deviceToken) {
    if (deviceToken == null || deviceToken.length() == 0)
      throw new BadRequestException("Missing parameter: " + UrlParams.DEVICE_TOKEN);
    if (!Matcher.isDeviceToken(deviceToken))
      throw new BadRequestException("Invalid device token format: " + deviceToken);
  }
}
