package com.scolaapp.api.model;

import java.lang.reflect.Field;
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

    
    @SuppressWarnings("unchecked")
    private <T extends ScCachedEntity> void setRelationshipKeys(T entity)
    {
        try {
            Field[] fields = entity.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (classOfField.getSuperclass() == ScCachedEntity.class) {
                    ScCachedEntity referencedEntity = (ScCachedEntity)field.get(entity);
                    
                    if (referencedEntity != null) {
                        Field keyField = entity.getClass().getField(field.getName() + "Key");
                        keyField.set(entity, new Key<T>((Class<T>)classOfField, referencedEntity.entityId));
                    }
                }
            }
        }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
        catch (NoSuchFieldException e) { throw new RuntimeException(e); }
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
        
        for (ScCachedEntity entity : entities) {
            setRelationshipKeys(entity);
        }
        
        DAO.ofy().put(entities);
        
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }
}
