package co.origon.api.model;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import co.origon.api.OrigonApplication;
import co.origon.api.auth.OAuthMeta;
import co.origon.api.helpers.*;

import static co.origon.api.helpers.InputValidator.*;

@Path("model")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReplicationController
{
    private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

    @POST
    @Path("replicate")
    public Response replicate(
            List<OReplicatedEntity> entitiesToReplicate,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date replicationDate,
            @QueryParam(UrlParams.AUTH_TOKEN) String authToken,
            @QueryParam(UrlParams.APP_VERSION) String appVersion,
            @QueryParam(UrlParams.LANGUAGE) String language
    ) {
        final OAuthMeta authMeta = checkAuthToken(authToken);
        final String metadata = checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);
        checkReplicationDate(replicationDate);
        checkLanguage(language);

        Dao.getDao().replicateEntities(entitiesToReplicate, authMeta.email, new Mailer(language));
        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(authMeta.email, replicationDate);
        final List<OReplicatedEntity> entitiesToReturn = fetchedEntities.stream()
                .filter(entity -> !entitiesToReplicate.contains(entity))
                .collect(Collectors.toList());

        LOG.fine(metadata + entitiesToReplicate.size() + "/" + entitiesToReturn.size() + " entities replicated");

        return Response
                .ok(entitiesToReturn)
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
        final OAuthMeta authMeta = checkAuthToken(authToken);
        final String metadata = checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);
        checkReplicationDate(replicationDate);

        final List<OReplicatedEntity> fetchedEntities = Dao.getDao().fetchEntities(authMeta.email, replicationDate);

        LOG.fine(metadata + fetchedEntities.size() + " entities fetched");

        return Response
                .ok(fetchedEntities)
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
        final OAuthMeta authMeta = checkAuthToken(authToken);
        checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);

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
        final OAuthMeta authMeta = checkAuthToken(authToken);
        checkMetadata(authMeta.deviceId, authMeta.deviceType, appVersion);

        OOrigo origo = Dao.getDao().lookupOrigo(internalJoinCode);
        if (origo == null) {
            throw new NotFoundException();
        }

        return Response
                .ok(origo)
                .build();
    }
}
