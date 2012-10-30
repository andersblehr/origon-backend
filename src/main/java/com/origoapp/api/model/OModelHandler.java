package com.origoapp.api.model;

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

import com.origoapp.api.aux.OLog;
import com.origoapp.api.aux.OMeta;
import com.origoapp.api.aux.OURLParams;


@Path("model")
public class OModelHandler
{
    private final static int SC_MULTI_STATUS = 207;
    
    
    @POST
    @Path("replicate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replicate(List<OReplicatedEntity> entitiesToReplicate,
                              @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date lastReplicationDate,
                              @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                              @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion);
        
        m.validateLastReplicationDate(lastReplicationDate);
        
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

            List<OReplicatedEntity> fetchedEntities = m.getDAO().fetchEntities(lastReplicationDate);
            
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
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if ((entitiesToReplicate.size() > 0) && (entitiesToReturn.size() > 0)) {
            return Response.status(SC_MULTI_STATUS).entity(entitiesToReturn).lastModified(replicationDate).build();
        } else if (entitiesToReplicate.size() > 0) {
            return Response.status(HttpServletResponse.SC_CREATED).lastModified(replicationDate).build();
        } else if (entitiesToReturn.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(entitiesToReturn).lastModified(replicationDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("fetch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchEntities(@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) Date lastReplicationDate,
                                  @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                  @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion);
        
        m.validateLastReplicationDate(lastReplicationDate);
        
        Date replicationDate = new Date();
        List<OReplicatedEntity> fetchedEntities = null;
        
        if (m.isValid()) {
            fetchedEntities = m.getDAO().fetchEntities(lastReplicationDate);
        } else {
            OLog.log().warning(m.meta() + "Invalid parameter set (see preceding warnings). Blocking entry for potential intruder, raising BAD_REQUEST (400).");
            OLog.throwWebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (fetchedEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(fetchedEntities).lastModified(replicationDate).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_MODIFIED).lastModified(replicationDate).build();
        }
    }
    
    
    @GET
    @Path("member/{memberId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchMember(@PathParam("memberId") String memberId,
                                @QueryParam(OURLParams.AUTH_TOKEN) String authToken,
                                @QueryParam(OURLParams.APP_VERSION) String appVersion)
    {
        OMeta m = new OMeta(authToken, appVersion);
        
        List<OReplicatedEntity> memberEntities = null;
        
        if (m.isValid()) {
            memberEntities = m.getDAO().lookupMember(memberId);
        }
        
        if (memberEntities.size() > 0) {
            return Response.status(HttpServletResponse.SC_OK).entity(memberEntities).build();
        } else {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

}
