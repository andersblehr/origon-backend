package co.origon.api.controller;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Matcher;
import co.origon.api.common.UrlParams;
import co.origon.api.filter.SupportedLanguage;
import co.origon.api.filter.ValidBasicAuthCredentials;
import co.origon.api.filter.ValidDeviceToken;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Singleton
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@ValidBasicAuthCredentials
public class AuthController {

  @Context private final ContainerRequestContext requestContext;

  @Inject private final AuthService authService;
  @Inject private final ReplicationService replicationService;

  AuthController(
      AuthService authService,
      ReplicationService replicationService,
      ContainerRequestContext requestContext) {

    this.authService = authService;
    this.replicationService = replicationService;
    this.requestContext = requestContext;
  }

  @GET
  @Path("register")
  @SupportedLanguage
  public Response registerUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.LANGUAGE) String language) {

    checkDeviceIdParam(deviceId);

    return Response.status(Status.CREATED)
        .entity(authService.registerUser(getCredentials(), deviceId, Language.fromCode(language)))
        .build();
  }

  @GET
  @Path("activate")
  public Response activateUser(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.DEVICE_ID) String deviceId,
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType) {

    checkDeviceCredentialsParams(deviceToken, deviceId, deviceType);
    final BasicAuthCredentials basicAuthCredentials = getCredentials();
    final MemberProxy userProxy =
        authService.activateUser(
            DeviceCredentials.builder()
                .email(basicAuthCredentials.email())
                .authToken(deviceToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build(),
            basicAuthCredentials.passwordHash());

    return replicationService
        .fetch(basicAuthCredentials.email())
        .filter(entities -> entities.size() > 0)
        .map(Response::ok)
        .orElse(Response.noContent())
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
      @QueryParam(UrlParams.DEVICE_TYPE) String deviceType) {

    checkDeviceCredentialsParams(deviceToken, deviceId, deviceType);
    final BasicAuthCredentials basicAuthCredentials = getCredentials();
    final MemberProxy userProxy =
        authService.loginUser(
            DeviceCredentials.builder()
                .email(basicAuthCredentials.email())
                .authToken(deviceToken)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .build(),
            basicAuthCredentials.passwordHash());

    return replicationService
        .fetch(basicAuthCredentials.email(), replicationDate)
        .filter(entities -> entities.size() > 0)
        .map(Response::ok)
        .orElse(Response.ok())
        .header(HttpHeaders.LOCATION, userProxy.memberId())
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("change")
  @ValidDeviceToken
  public Response changePassword(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken) {

    authService.changePassword(getCredentials());

    return Response.status(Status.CREATED).build();
  }

  @GET
  @Path("reset")
  @ValidDeviceToken
  @SupportedLanguage
  public Response resetPassword(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.LANGUAGE) String language) {

    authService.resetPassword(getCredentials(), Language.fromCode(language));

    return Response.status(Status.CREATED).build();
  }

  @GET
  @Path("sendcode")
  @ValidDeviceToken
  @SupportedLanguage
  public Response sendActivationCode(
      @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.LANGUAGE) String language) {

    return Response.status(Status.CREATED)
        .entity(
            authService.sendActivationCode(
                getCredentials(), deviceToken, Language.fromCode(language)))
        .build();
  }

  private BasicAuthCredentials getCredentials() {
    return (BasicAuthCredentials) requestContext.getProperty(BasicAuthCredentials.CONTEXT_KEY);
  }

  private static void checkDeviceIdParam(String deviceId) {
    if (deviceId == null || deviceId.length() == 0) {
      throw new BadRequestException("Missing parameter: " + UrlParams.DEVICE_ID);
    }
  }

  private static void checkDeviceCredentialsParams(
      String deviceToken, String deviceId, String deviceType) {

    if (deviceToken == null || deviceToken.length() == 0) {
      throw new BadRequestException("Missing parameter: " + UrlParams.DEVICE_TOKEN);
    }
    if (!Matcher.isDeviceToken(deviceToken)) {
      throw new BadRequestException("Invalid device token format: " + deviceToken);
    }
    checkDeviceIdParam(deviceId);
    if (deviceType == null || deviceType.length() == 0) {
      throw new BadRequestException("Missing parameter: " + UrlParams.DEVICE_TYPE);
    }
  }
}
