package com.scolaapp.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.googlecode.objectify.Key;
import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("model")
public class ScModelHandler
{
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam("duid")    String deviceId,
                                    @QueryParam("device")  String deviceType,
                                    @QueryParam("version") String appVersion)
    {
        ScDAO DAO = new ScDAO(deviceId, deviceType, appVersion);
        
        ScLog.log().fine(DAO.meta() + String.format("Data: %s", entities.toString()));
        Date now = new Date();
        
        for (ScCachedEntity entity : entities) {
            entity.mapRelationshipKeys();
            
            if (!entity.isReferenceToSharedEntity()) {
                entity.dateModified = now;
            }
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
    
    
    @GET
    @Path("fetch")
    @Produces({MediaType.APPLICATION_JSON})
    public Response fetchEntities(@QueryParam("duid")    String deviceId,
                                  @QueryParam("device")  String deviceType,
                                  @QueryParam("version") String appVersion,
                                  @QueryParam("uid")     String userId)
    {
        ScDAO DAO = new ScDAO(deviceId, deviceType, appVersion);
        
        List<ScCachedEntity> updatedEntities = new ArrayList<ScCachedEntity>();
        
        Key<ScScolaMember> memberKey = new Key<ScScolaMember>(ScScolaMember.class, userId);
        List<ScScolaMembership> memberships = DAO.ofy().query(ScScolaMembership.class).filter("memberKey", memberKey).list();
        
        for (ScScolaMembership membership : memberships) {
            if (membership.isActive) {
                List<ScCachedEntity> updatedEntitiesInScola = DAO.ofy().query(ScCachedEntity.class).filter("scolaKey", membership.scolaKey).filter("dateModified", "> lastFetchDate").list();
                List<Key<ScCachedEntity>> sharedEntityKeys = new ArrayList<Key<ScCachedEntity>>(); 
                
                for (ScCachedEntity entity : updatedEntitiesInScola) {
                    if (entity.isReferenceToSharedEntity()) {
                        ScSharedEntityRef entityRef = (ScSharedEntityRef)entity;
                        sharedEntityKeys.add(entityRef.sharedEntityKey);
                    } else {
                        updatedEntities.add(entity);
                    }
                }
                
                if (sharedEntityKeys.size() > 0) {
                    Map<Key<ScCachedEntity>, ScCachedEntity> sharedEntitiesMap = DAO.ofy().get(sharedEntityKeys);
                    
                    for (Map.Entry<Key<ScCachedEntity>, ScCachedEntity> sharedEntityEntry : sharedEntitiesMap.entrySet()) {
                        updatedEntities.add(sharedEntityEntry.getValue());
                    }
                }
            }
        }
        
        return Response.status(HttpServletResponse.SC_OK).entity(updatedEntities).build();
    }
}
