package com.origoapp.api.model;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;


@Entity
@Unindexed
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "entityClass")
@JsonSubTypes({
    @Type(value = OCachedEntityGhost.class, name = "OCachedEntityGhost"),
    @Type(value = ODevice.class, name = "ODevice"),
    @Type(value = OMember.class, name = "OMember"),
    @Type(value = OMemberResidency.class, name = "OMemberResidency"),
    @Type(value = OMembership.class, name = "OMembership"),
    @Type(value = OMessageBoard.class, name = "OMessageBoard"),
    @Type(value = OOrigo.class, name = "OOrigo"),
    @Type(value = OSharedEntityRef.class, name = "OSharedEntityRef")})
public abstract class OCachedEntity
{
    public @Parent Key<OOrigo> origoKey;
    public @Id String entityId;
    
    public @NotSaved String origoId;
    public @NotSaved String entityClass;
    
    public Date dateCreated;
    public @Indexed Date dateModified;
    public @NotSaved(IfNull.class) Date dateExpires;
    
    public @NotSaved(IfFalse.class) boolean isShared = false;
    
    
    @PrePersist
    @SuppressWarnings("unchecked")
    public <T extends OCachedEntity> void internaliseRelationships()
    {
        try {
            Field[] fields = this.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (classOfField.getSuperclass() == OCachedEntity.class) {
                    OCachedEntity referencedEntity = (OCachedEntity)field.get(this);
                    
                    if (referencedEntity != null) {
                        Field keyField = this.getClass().getField(field.getName() + "Key");
                        keyField.set(this, new Key<T>(origoKey, (Class<T>)classOfField, referencedEntity.entityId));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }
    
    
    @PostLoad
    @SuppressWarnings("unchecked")
    public <T extends OCachedEntity> void externaliseRelationships()
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
                
                if (classOfField.getSuperclass() == OCachedEntity.class) {
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
            
            Field entityClassField = this.getClass().getField("entityClass");
            entityClassField.set(this, this.getClass().getSimpleName());
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
        
        if (OCachedEntity.class.isAssignableFrom(other.getClass())) {
            areEqual = (((OCachedEntity)other).hashCode() == this.hashCode());
        }
        
        return areEqual;
    }
    
    
    @Override
    public int hashCode()
    {
        return String.format("%s$%s", origoId, entityId).hashCode();
    }
}