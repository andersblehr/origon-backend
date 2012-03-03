package com.scolaapp.api.model;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.googlecode.objectify.Key;

import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("model")
public class ScModelHandler
{
    private String deviceId;
    private ScDAO DAO;

    
    private <T extends ScCachedEntity> Key<T> keyForEntity(Class<T> clazz, T entityRef)
    {
        Key<T> entityKey = null;
        
        if (entityRef != null) {
            entityKey = new Key<T>(clazz, entityRef.entityId);
        }
        
        return entityKey;
    }
    
    
    private void setRelationshipKeys(ScDeviceListing deviceListing)
    {
        deviceListing.deviceKey = keyForEntity(ScDevice.class, deviceListing.device);
        deviceListing.memberKey = keyForEntity(ScScolaMember.class, deviceListing.member);
    }
    
    
    private void setRelationshipKeys(ScHouseholdResidency residency)
    {
        residency.householdKey = keyForEntity(ScHousehold.class, residency.household);
        residency.residentKey = keyForEntity(ScScolaMember.class, residency.resident);
    }
    
    
    private void setRelationshipKeys(ScScola scola)
    {
        scola.guardedScolaKey = keyForEntity(ScScola.class, scola.guardedScola);
        scola.guardianScolaKey = keyForEntity(ScScola.class, scola.guardianScola);
    }
    
    
    private void setRelationshipKeys(ScScolaMembership membership)
    {
        membership.memberKey = keyForEntity(ScScolaMember.class, membership.member);
        membership.scolaKey = keyForEntity(ScScola.class, membership.scola);
    }
    
    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                @QueryParam ("duid")    String deviceUUID,
                                @QueryParam ("device")  String deviceType,
                                @QueryParam ("version") String appVersion)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);
        
        ScLog.log().fine(String.format("%s Data: %s", ScLog.meta(deviceId), entities.toString()));
        
        for (ScCachedEntity entity: entities) {
            Class<?> entityClass = entity.getClass();
            
            if (entityClass.equals(ScDeviceListing.class)) {
                setRelationshipKeys((ScDeviceListing)entity);
            } else if (entityClass.equals(ScHouseholdResidency.class)) {
                setRelationshipKeys((ScHouseholdResidency)entity);
            } else if (entityClass.equals(ScScola.class)) {
                setRelationshipKeys((ScScola)entity);
            } else if (entityClass.equals(ScScolaMembership.class)) {
                setRelationshipKeys((ScScolaMembership)entity);
            }
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(201).build();
    }
}
