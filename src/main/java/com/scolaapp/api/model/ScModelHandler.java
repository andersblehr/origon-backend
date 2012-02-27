package com.scolaapp.api.model;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.googlecode.objectify.Key;

import com.scolaapp.api.utils.ScDAO;
import com.scolaapp.api.utils.ScLog;
import com.scolaapp.api.model.relationships.ScScolaListing;
import com.scolaapp.api.model.relationships.ScScolaMembership;


@Path("model")
public class ScModelHandler
{
    private String deviceId;
    private ScDAO DAO;
    
    private HashMap<String, ScCachedEntity> entityLookupMap = new HashMap<String, ScCachedEntity>(); 

    
    private <T extends ScCachedEntity> Key<T> keyForEntity(Class<T> clazz, T entityRef)
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
    
    
    private void persistPerson(ScPerson person)
    {
        person.householdKey = keyForEntity(ScHousehold.class, person.household);
        
        DAO.ofy().put(person);
        person.isDirty = false;
 
        Key<ScPerson> personKey = new Key<ScPerson>(ScPerson.class, person.entityId);
    }
    
    
    private void persistScola(ScScola scola)
    {
        scola.guardedScolaKey = keyForEntity(ScScola.class, scola.guardedScola);
        scola.guardianScolaKey = keyForEntity(ScScola.class, scola.guardianScola);
        
        DAO.ofy().put(scola);
        scola.isDirty = false;
        
        Key<ScScola> scolaKey = new Key<ScScola>(ScScola.class, scola.entityId);
        
        for (int i = 0; i < scola.admins.size(); i++) {
            Key<ScScolaMember> adminKey = keyForEntity(ScScolaMember.class, scola.admins.get(i));
            DAO.ofy().put(new ScScolaMembership(adminKey, scolaKey, true));
        }
        
        for (int i = 0; i < scola.membersActive.size(); i++) {
            Key<ScScolaMember> memberKey = keyForEntity(ScScolaMember.class, scola.membersActive.get(i));
            DAO.ofy().put(new ScScolaMembership(memberKey, scolaKey, false));
        }
        
        for (int i = 0; i < scola.membersInactive.size(); i++) {
            Key<ScPerson> personKey = keyForEntity(ScPerson.class, scola.membersInactive.get(i));
            DAO.ofy().put(new ScScolaListing(personKey, scolaKey));
        }
    }
    
    
    private void persistScolaMember(ScScolaMember scolaMember)
    {
        DAO.ofy().put(scolaMember);
        scolaMember.isDirty = false;
        
        Key<ScScolaMember> memberKey = new Key<ScScolaMember>(ScScolaMember.class, scolaMember.entityId);
    }
    
    
    private void persistEntity(ScCachedEntity entity)
    {
        if (entity.isDirty) {
            if (entity.getClass().equals(ScPerson.class)) {
                persistPerson((ScPerson)entity);
            } else if (entity.getClass().equals(ScScola.class)) {
                persistScola((ScScola)entity);
            } else if (entity.getClass().equals(ScScolaMember.class)) {
                persistScolaMember((ScScolaMember)entity);
            }
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
