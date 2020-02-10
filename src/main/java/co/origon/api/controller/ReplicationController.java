package co.origon.api.controller;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import co.origon.api.common.Mailer.Language;
import co.origon.api.common.UrlParams;
import co.origon.api.filter.SupportedLanguage;
import co.origon.api.filter.ValidDeviceToken;
import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.service.AuthService;
import co.origon.api.service.ReplicationService;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
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
public class ReplicationController {

  private static final Logger LOG = Logger.getLogger(ReplicationController.class.getName());

  @Inject private ReplicationService replicationService;
  @Inject private AuthService authService;

  private final Supplier<RuntimeException> unknownDeviceTokenThrower =
      () -> new NotAuthorizedException("Unknown device token");

  @POST
  @Path("replicate")
  @SupportedLanguage
  public Response replicate(
      List<ReplicatedEntity> entities,
      @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken,
      @QueryParam(UrlParams.LANGUAGE) String language) {

    checkReplicationDate(replicationDate);
    final String userEmail =
        authService
            .getDeviceCredentials(deviceToken)
            .orElseThrow(unknownDeviceTokenThrower)
            .email();
    replicationService.replicate(entities, userEmail, Language.fromCode(language));

    return replicationService
        .fetch(userEmail, replicationDate)
        .map(
            fetchedEntities ->
                fetchedEntities.stream()
                    .filter(fetchedEntity -> !entities.contains(fetchedEntity))
                    .collect(Collectors.toList()))
        .map(this::logReplicated)
        .map(Response::ok)
        .orElse(Response.status(Status.CREATED))
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("fetch")
  public Response fetch(
      @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken) {

    checkReplicationDate(replicationDate);
    final String userEmail =
        authService
            .getDeviceCredentials(deviceToken)
            .orElseThrow(unknownDeviceTokenThrower)
            .email();

    return replicationService
        .fetch(userEmail, replicationDate)
        .map(this::logReplicated)
        .map(Response::ok)
        .orElse(Response.ok())
        .lastModified(new Date())
        .build();
  }

  @GET
  @Path("member")
  public Response lookupMember(
      @QueryParam(UrlParams.IDENTIFIER) String email,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken) {

    return replicationService
        .lookupMember(email)
        .map(memberEntities -> Response.ok(memberEntities).build())
        .orElseThrow(NotFoundException::new);
  }

  @GET
  @Path("origo")
  public Response lookupOrigo(
      @QueryParam(UrlParams.IDENTIFIER) String internalJoinCode,
      @QueryParam(UrlParams.DEVICE_TOKEN) String deviceToken) {

    return replicationService
        .lookupOrigo(internalJoinCode)
        .map(origo -> Response.ok(origo).build())
        .orElseThrow(NotFoundException::new);
  }

  private static void checkReplicationDate(Date replicationDate) {
    try {
      checkNotNull(replicationDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
      checkArgument(replicationDate.before(new Date()), "Last replication is in the future");
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new BadRequestException(e.getMessage(), e);
    }
  }

  private List<ReplicatedEntity> logReplicated(List<ReplicatedEntity> entities) {
    LOG.info("Returning " + entities.size() + " entities");
    return entities;
  }
}
