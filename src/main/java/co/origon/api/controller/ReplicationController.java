package co.origon.api.controller;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.filter.SupportedLanguage;
import co.origon.api.filter.ValidDeviceToken;
import co.origon.api.filter.ValidSessionData;
import co.origon.api.model.DeviceCredentials;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Origo;
import co.origon.api.model.api.entity.ReplicatedEntity;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Singleton
@Path("model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ValidDeviceToken
@ValidSessionData
public class ReplicationController {

  @Inject private ReplicationService replicationService;
  @Inject private AuthService authService;
  @Inject private DaoFactory daoFactory;
  @Inject private Mailer mailer;

  private final Supplier<RuntimeException> unknownDeviceTokenThrower =
      () -> new NotAuthorizedException("Unknown device token");

  @POST
  @Path("replicate")
  @SupportedLanguage
  public Response replicate(
      List<ReplicatedEntity> entities,
      @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion,
      @QueryParam(UrlParams.LANGUAGE) String language) {
    final DeviceCredentials deviceCredentials =
        authService.getDeviceCredentials(deviceToken).orElseThrow(unknownDeviceTokenThrower);
    checkReplicationDate(replicationDate);

    replicationService.replicate(entities, deviceCredentials.email(), Language.fromCode(language));
    final List<ReplicatedEntity> returnableEntities =
        replicationService.fetch(deviceCredentials.email(), replicationDate).stream()
            .filter(entity -> !entities.contains(entity))
            .collect(Collectors.toList());

    Session.log(entities.size() + "+" + returnableEntities.size() + " entities replicated");

    return Response.status(entities.size() > 0 ? Status.CREATED : Status.OK)
        .entity(returnableEntities.size() > 0 ? returnableEntities : null)
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("fetch")
  public Response fetch(
      @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    final DeviceCredentials deviceCredentials =
        authService.getDeviceCredentials(deviceToken).orElseThrow(unknownDeviceTokenThrower);
    checkReplicationDate(replicationDate);

    final List<ReplicatedEntity> fetchedEntities =
        replicationService.fetch(deviceCredentials.email(), replicationDate);
    Session.log(fetchedEntities.size() + " entities fetched");

    return Response.ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("member")
  public Response lookupMember(
      @QueryParam(UrlParams.IDENTIFIER) String email,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    List<ReplicatedEntity> memberEntities = replicationService.lookupMember(email);
    if (memberEntities == null) {
      throw new NotFoundException();
    }

    return Response.ok(memberEntities).build();
  }

  @GET
  @Path("origo")
  public Response lookupOrigo(
      @QueryParam(UrlParams.IDENTIFIER) String internalJoinCode,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.APP_VERSION) String appVersion) {
    Origo origo = replicationService.lookupOrigo(internalJoinCode);
    if (origo == null) {
      throw new NotFoundException();
    }

    return Response.ok(origo).build();
  }

  private static void checkReplicationDate(Date replicationDate) {
    try {
      checkNotNull(replicationDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
      checkArgument(
          replicationDate.before(new Date()), "Invalid last replication date: " + replicationDate);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new BadRequestException(e.getMessage(), e);
    }
  }
}
