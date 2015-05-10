package co.origon.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import co.origon.api.aux.OLog;
import co.origon.api.aux.OMeta;
import co.origon.api.aux.OURLParams;


@Path("model")
public class OModelHandler
{
    private final static int SC_MULTI_STATUS = 207;
    
    
    @POST
    @Path("replicate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replicate(List<OReplicatedEntity> entitiesToReplicate,
                              @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date deviceReplicationDate,
                              @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                              @QueryParam(OURLParams.APP_VERSION) String appVersion,
                              @QueryParam(OURLParams.LANGUAGE) String language)
    {
        OMeta m = new OMeta(authToken, appVersion, language);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateReplicationDate(deviceReplicationDate);
        
        Date replicationDate = null;
        
        Set<String> replicatedEntityIds = new HashSet<String>();
        List<OReplicatedEntity> entitiesToReturn = new ArrayList<OReplicatedEntity>();
        
        if (m.isValid()) {
            if (entitiesToReplicate.size() > 0) {
                for (OReplicatedEntity entity : entitiesToReplicate) {
                    replicatedEntityIds.add(entity.entityId);
                }
                
                m.getDAO().replicateEntities(entitiesToReplicate);
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            replicationDate = new Date();

            List<OReplicatedEntity> fetchedEntities = m.getDAO().fetchEntities(deviceReplicationDate);
            
            if (entitiesToReplicate.size() > 0) {
                for (OReplicatedEntity entity : fetchedEntities) {
                    if (!replicatedEntityIds.contains(entity.entityId)) {
                        entitiesToReturn.add(entity);
                    }
                }
            } else {
                entitiesToReturn = fetchedEntities;
            }
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising UNAUTHORIZED (401).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        OLog.log().fine(m.meta() + "Replicated entities: " + entitiesToReplicate.toString());
        OLog.log().fine(m.meta() + "Fetched entities: " + entitiesToReturn.toString());
        
        if (entitiesToReplicate.size() > 0 && entitiesToReturn.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 207");
            return Response.status(OModelHandler.SC_MULTI_STATUS).entity(entitiesToReturn).lastModified(replicationDate).build();
        } else if (entitiesToReplicate.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 201");
            return Response.status(HttpServletResponse.SC_CREATED).lastModified(replicationDate).build();
        } else if (entitiesToReturn.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).entity(entitiesToReturn).lastModified(replicationDate).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 304");
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("fetch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchEntities(@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date deviceReplicationDate,
                                  @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                  @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        m.validateReplicationDate(deviceReplicationDate);
        
        Date replicationDate = new Date();
        List<OReplicatedEntity> fetchedEntities = null;
        
        if (m.isValid()) {
            fetchedEntities = m.getDAO().fetchEntities(deviceReplicationDate);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising UNAUTHORIZED (401).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        OLog.log().fine(m.meta() + "Fetched entities: " + fetchedEntities.toString());
        
        if (fetchedEntities.size() > 0) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 304");
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupMember(@QueryParam(OURLParams.IDENTIFIER) String memberId,
                                 @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                 @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        List<OReplicatedEntity> memberEntities = null;
        
        if (m.isValid()) {
            memberEntities = m.getDAO().lookupMemberEntities(memberId);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising UNAUTHORIZED (401).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        if (memberEntities != null) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).entity(memberEntities).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 404");
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }
    
    
    @GET
    @Path("origo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupOrigo(@QueryParam(OURLParams.IDENTIFIER) String internalJoinCode,
                                @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion, null);
        
        if (m.isDownForMaintenance()) {
            OLog.log().warning(m.meta() + "Service is down for maintenance, raising SERVICE_UNAVAILABLE (503).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        OOrigo origo = null;;
        
        if (m.isValid()) {
            origo = m.getDAO().lookupOrigo(internalJoinCode);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising UNAUTHORIZED (401).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        if (origo != null) {
            OLog.log().fine(m.meta() + "HTTP status: 200");
            return Response.status(HttpServletResponse.SC_OK).entity(origo).build();
        } else {
            OLog.log().fine(m.meta() + "HTTP status: 404");
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }
}