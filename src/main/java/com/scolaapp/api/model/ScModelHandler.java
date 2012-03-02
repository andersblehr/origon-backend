package com.scolaapp.api.model;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.googlecode.objectify.Key;

import com.scolaapp.api.model.relationships.ScDeviceListing;
import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;


@Path("model")
public class ScModelHandler
{
    private String deviceId;
    private ScDAO DAO;
    
    private HashMap<String, ScCachedEntity> entityLookupMap = new HashMap<String, ScCachedEntity>(); 

    
    private <T extends ScCachedEntity> Key<T> persistEntity(Class<T> clazz, T entityRef)
    {
        Key<T> entityKey = null;
        
        if (entityRef != null) {
            T entity = clazz.cast(entityLookupMap.get(entityRef.entityId));
            
            if ((entity != null) && entity.isDirty) {
                persistEntity(entity);
            }
            
            entityKey = new Key<T>(clazz, entityRef.entityId);
        }
        
        return entityKey;
    }
    
    
    private void persistDevice(ScDevice device)
    {
        // TODO: Device name should be on ScDeviceListing. 
    }
    
    
    private void persistScola(ScScola scola)
    {
        scola.guardedScolaKey = persistEntity(ScScola.class, scola.guardedScola);
        scola.guardianScolaKey = persistEntity(ScScola.class, scola.guardianScola);
        
        DAO.ofy().put(scola);
        scola.isDirty = false;
    }
    
    
    private void persistScolaMember(ScScolaMember member)
    {
        member.householdKey = persistEntity(ScHousehold.class, member.household);
        
        DAO.ofy().put(member);
        member.isDirty = false;
        
        Key<ScScolaMember> memberKey = new Key<ScScolaMember>(ScScolaMember.class, member.entityId);
        
        for (ScDevice deviceRef: member.devices) {
            Key<ScDevice> deviceKey = persistEntity(ScDevice.class, deviceRef);
            DAO.ofy().put(new ScDeviceListing(deviceKey, memberKey));
        }
    }
    
    
    private void persistScolaMembership(ScScolaMembership membership)
    {
        membership.memberKey = persistEntity(ScScolaMember.class, membership.member);
        membership.scolaKey = persistEntity(ScScola.class, membership.scola);
        
        DAO.ofy().put(membership);
        membership.isDirty = false;
    }
    
    
    private void persistEntity(ScCachedEntity entity)
    {
        if (entity.isDirty) {
            if (entity.getClass().equals(ScDevice.class)) {
                persistDevice((ScDevice)entity);
            } else if (entity.getClass().equals(ScScola.class)) {
                persistScola((ScScola)entity);
            } else if (entity.getClass().equals(ScScolaMember.class)) {
                persistScolaMember((ScScolaMember)entity);
            } else if (entity.getClass().equals(ScScolaMembership.class)) {
                persistScolaMembership((ScScolaMembership)entity);
            }
            
            ScLog.log().fine(String.format("%s Persisted entity {%s} (%s)", ScLog.meta(deviceId), entity.entityId, entity.getClass().getName()));
        }
    }
    
    
    @POST
    @Path("persist")
    @Consumes({MediaType.APPLICATION_JSON})
    public void persistEntities(List<ScCachedEntity> entities,
                                @QueryParam ("duid")    String deviceUUID,
                                @QueryParam ("device")  String deviceType,
                                @QueryParam ("version") String appVersion)
    {
        deviceId = deviceUUID;
        ScLog.setMeta(deviceId, deviceType, appVersion);
        DAO = new ScDAO(deviceId);
        
        ScLog.log().fine(String.format("%s Data: %s", ScLog.meta(deviceId), entities.toString()));
        
        for (int i = 0; i < entities.size(); i++) {
            ScCachedEntity entity = entities.get(i);
            
            entity.isDirty = true;
            entityLookupMap.put(entity.entityId, entity);
        }
        
        for (int i = 0; i < entities.size(); i++) {
            persistEntity(entities.get(i));
        }
        
        //ScLog.log().fine(String.format("name: %s", member.name));
        //ScLog.log().fine(String.format("email: %s", member.email));
        //ScLog.log().fine(String.format("date of birth: %s", member.dateOfBirth.toString()));
        //ScLog.log().fine(String.format("gender: %s", member.gender));
        //ScLog.log().fine(String.format("address: %s, %s", member.household.addressLine1, member.household.postCodeAndCity));
    }
}
