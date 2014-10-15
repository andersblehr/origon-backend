package com.origoapp.api.model;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@Entity
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "entityClass")
@JsonSubTypes({
    @Type(value = ODevice.class, name = "ODevice"),
    @Type(value = OMember.class, name = "OMember"),
    @Type(value = OMembership.class, name = "OMembership"),
    @Type(value = OOrigo.class, name = "OOrigo"),
    @Type(value = OReplicatedEntityRef.class, name = "OReplicatedEntityRef"),
    @Type(value = OSettings.class, name = "OSettings")})
public abstract class OReplicatedEntity
{
    public @Parent Key<OOrigo> origoKey;
    public @Id String entityId;
    
    public @Ignore String origoId;
    public @Ignore String entityClass;
    public @IgnoreSave(IfFalse.class) boolean isExpired;
    
    public String createdBy;
    public String modifiedBy;
    public Date dateCreated;
    public @Index Date dateReplicated;
    public @IgnoreSave(IfNull.class) Date dateExpires;
    
    
    @OnSave
    @SuppressWarnings("unchecked")
    public <T extends OReplicatedEntity> void internaliseRelationships()
    {
        try {
            Field[] fields = this.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (OReplicatedEntity.class.isAssignableFrom(classOfField)) {
                    OReplicatedEntity referencedEntity = (OReplicatedEntity)field.get(this);
                    
                    if (referencedEntity != null) {
                        Field keyField = this.getClass().getField(field.getName() + "Key");
                        keyField.set(this, Key.create(origoKey, (Class<T>)classOfField, referencedEntity.entityId));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }
    
    
    @OnLoad
    @SuppressWarnings("unchecked")
    public <T extends OReplicatedEntity> void externaliseRelationships()
    {
        try {
            origoId = origoKey.getRaw().getName();
            
            Field origoRefField = getOrigoRefField();
            
            if (origoRefField != null) {
                origoRefField.set(this, createEntityRef("OOrigo", origoId));
            }

            Field[] fields = this.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (OReplicatedEntity.class.isAssignableFrom(classOfField)) {
                    Field keyField = this.getClass().getField(field.getName() + "Key");
                    Key<T> referencedEntityKey = (Key<T>)keyField.get(this);
                    
                    if (referencedEntityKey != null) {
                        String referencedEntityClassName = classOfField.getSimpleName();
                        String referencedEntityId = referencedEntityKey.getRaw().getName();
                        
                        Field refField = this.getClass().getField(field.getName() + "Ref");
                        refField.set(this, createEntityRef(referencedEntityClassName, referencedEntityId));
                    }
                }
            }
            
            entityClass = this.getClass().getSimpleName();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    
    private Field getOrigoRefField()
    {
        try {
            return this.getClass().getField("origoRef");
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
    
    
    private Map<String, String> createEntityRef(String entityClass, String entityId)
    {
        Map<String, String> entityRef = new HashMap<String, String>();
        
        entityRef.put("entityClass", entityClass);
        entityRef.put("entityId", entityId);
        
        return entityRef;
    }
    
    
    @Override
    public boolean equals(Object other)
    {
        boolean areEqual = false;
        
        if (OReplicatedEntity.class.isAssignableFrom(other.getClass())) {
            areEqual = (((OReplicatedEntity)other).hashCode() == this.hashCode());
        }
        
        return areEqual;
    }
    
    
    @Override
    public int hashCode()
    {
        return String.format("%s$%s", origoId, entityId).hashCode();
    }
}
