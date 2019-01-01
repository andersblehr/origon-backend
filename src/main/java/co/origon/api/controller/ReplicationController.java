package co.origon.api.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.origon.api.annotation.LanguageSupported;
import co.origon.api.annotation.SessionDataValidated;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.common.*;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.annotation.TokenAuthenticated;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Path("model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@TokenAuthenticated
@SessionDataValidated
public class ReplicationController {

    @POST
    @Path("replicate")
    @LanguageSupported
    public Response replicate(
            List<OReplicatedEntity> entitiesToReplicate,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        checkReplicationDate(replicationDate);

        ODao.getDao().replicateEntities(entitiesToReplicate, authMeta.email(), Mailer.forLanguage(language));
        final List<OReplicatedEntity> fetchedEntities = ODao.getDao().fetchEntities(authMeta.email(), replicationDate);
        final List<OReplicatedEntity> entitiesToReturn = fetchedEntities.stream()
                .filter(entity -> !entitiesToReplicate.contains(entity))
                .collect(Collectors.toList());

        Session.log(entitiesToReplicate.size() + "+" + entitiesToReturn.size() + " entities replicated");

        return Response
                .status(entitiesToReplicate.size() > 0 ? Status.CREATED : Status.OK)
                .entity(entitiesToReturn.size() > 0 ? entitiesToReturn : null)
                .lastModified(new Date())
                .build();
    }

    @GET
    @Path("fetch")
    public Response fetchEntities(
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        checkReplicationDate(replicationDate);

        final List<OReplicatedEntity> fetchedEntities = ODao.getDao().fetchEntities(authMeta.email(), replicationDate);
        Session.log(fetchedEntities.size() + " entities fetched");

        return Response
                .ok(fetchedEntities.size() > 0 ? fetchedEntities : null)
                .lastModified(new Date())
                .build();
    }

    @GET
    @Path("member")
    public Response lookupMember(
            @QueryParam(UrlParams.IDENTIFIER) String memberId,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        List<OReplicatedEntity> memberEntities = ODao.getDao().lookupMemberEntities(memberId);
        if (memberEntities == null) {
            throw new NotFoundException();
        }

        return Response
                .ok(memberEntities)
                .build();
    }

    @GET
    @Path("origo")
    public Response lookupOrigo(
            @QueryParam(UrlParams.IDENTIFIER) String internalJoinCode,
            @QueryParam(UrlParams.DEVICE_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        OOrigo origo = ODao.getDao().lookupOrigo(internalJoinCode);
        if (origo == null) {
            throw new NotFoundException();
        }

        return Response
                .ok(origo)
                .build();
    }

    private static void checkReplicationDate(Date replicationDate) {
        try {
            checkNotNull(replicationDate, "Missing HTTP header: " + HttpHeaders.IF_MODIFIED_SINCE);
            checkArgument(replicationDate.before(new Date()), "Invalid last replication date: " + replicationDate);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }
}