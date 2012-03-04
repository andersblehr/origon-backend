package com.scolaapp.api.model;

import java.util.List;

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
    
    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response persistEntities(List<ScCachedEntity> entities,
                                    @QueryParam("duid")    String deviceUUID,
                                    @QueryParam("device")  String deviceType,
                                    @QueryParam("version") String appVersion)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);
        
        ScLog.log().fine(String.format("%s Data: %s", ScLog.meta(deviceId), entities.toString()));
        
        for (ScCachedEntity entity: entities) {
            Class<?> entityClass = entity.getClass();
            
            if (entityClass.equals(ScDeviceListing.class)) {
                ScDeviceListing deviceListing = (ScDeviceListing)entity;
                
                deviceListing.deviceKey = keyForEntity(ScDevice.class, deviceListing.device);
                deviceListing.memberKey = keyForEntity(ScScolaMember.class, deviceListing.member);
            } else if (entityClass.equals(ScHouseholdResidency.class)) {
                ScHouseholdResidency residency = (ScHouseholdResidency)entity;
                
                residency.householdKey = keyForEntity(ScHousehold.class, residency.household);
                residency.residentKey = keyForEntity(ScScolaMember.class, residency.resident);
            } else if (entityClass.equals(ScScola.class)) {
                ScScola scola = (ScScola)entity;
                
                scola.guardedScolaKey = keyForEntity(ScScola.class, scola.guardedScola);
                scola.guardianScolaKey = keyForEntity(ScScola.class, scola.guardianScola);
            } else if (entityClass.equals(ScScolaMember.class)) {
                ScScolaMember member = (ScScolaMember)entity;
                
                ScLog.log().fine(String.format("%s Date of birth: %s", ScLog.meta(deviceId), member.dateOfBirth.toString()));
                member.primaryResidenceKey = keyForEntity(ScHousehold.class, member.primaryResidence);
            } else if (entityClass.equals(ScScolaMembership.class)) {
                ScScolaMembership membership = (ScScolaMembership)entity;
                
                membership.memberKey = keyForEntity(ScScolaMember.class, membership.member);
                membership.scolaKey = keyForEntity(ScScola.class, membership.scola);
            }
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
}
