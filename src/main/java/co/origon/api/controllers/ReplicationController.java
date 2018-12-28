package co.origon.api.controllers;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.origon.api.OrigonApplication;
import co.origon.api.annotations.SessionDataValidated;
import co.origon.api.entities.OAuthMeta;
import co.origon.api.common.*;
import co.origon.api.entities.OOrigo;
import co.origon.api.entities.OReplicatedEntity;
import co.origon.api.annotations.TokenAuthenticated;

import static co.origon.api.common.InputValidator.*;

@Path("model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@TokenAuthenticated
@SessionDataValidated
public class ReplicationController {

    @POST
    @Path("replicate")
    public Response replicate(
            List<OReplicatedEntity> entitiesToReplicate,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        checkReplicationDate(replicationDate);
        checkLanguage(language);

        Dao.getDao().replicateEntities(entitiesToReplicate, authMeta.getEmail(), Mailer.forLanguage(language));
        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(authMeta.getEmail(), replicationDate);
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
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        final OAuthMeta authMeta = OAuthMeta.get(authToken);
        checkReplicationDate(replicationDate);

        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(authMeta.getEmail(), replicationDate);

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
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        List<OReplicatedEntity> memberEntities = Dao.getDao().lookupMemberEntities(memberId);
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
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion
    ) {
        OOrigo origo = Dao.getDao().lookupOrigo(internalJoinCode);
        if (origo == null) {
            throw new NotFoundException();
        }

        return Response
                .ok(origo)
                .build();
    }
}
