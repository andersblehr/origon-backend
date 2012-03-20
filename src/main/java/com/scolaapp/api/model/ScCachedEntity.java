package com.scolaapp.api.model;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfNotNull;
import com.googlecode.objectify.condition.IfNull;


@Entity
@Unindexed
@JsonTypeInfo (
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "entityClass")
@JsonSubTypes({
    @Type(value = ScDevice.class, name = "ScDevice"),
    @Type(value = ScDeviceListing.class, name = "ScDeviceListing"),
    @Type(value = ScHousehold.class, name = "ScHousehold"),
    @Type(value = ScHouseholdResidency.class, name = "ScHouseholdResidency"),
    @Type(value = ScMessageBoard.class, name = "ScMessageBoard"),
    @Type(value = ScScola.class, name = "ScScola"),
    @Type(value = ScScolaMember.class, name = "ScScolaMember"),
    @Type(value = ScScolaMembership.class, name = "ScScolaMembership")})
@JsonIgnoreProperties(value="scolaKey")
public abstract class ScCachedEntity
{
    public @Id String entityId;
    
    public Date dateCreated;
    public @NotSaved(IfNull.class) @Indexed(IfNotNull.class) Date dateModified;
    public @NotSaved(IfNull.class) Date dateExpires;
    
    public @NotSaved(IfNull.class) @Indexed(IfNotNull.class) Key<ScScola> scolaKey;
    public @NotSaved(IfNull.class) String scolaId;
    
    
    public ScCachedEntity() {}


    @SuppressWarnings("unchecked")
    public <T extends ScCachedEntity> void mapRelationshipKeys()
    {
        try {
            Field[] fields = this.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (classOfField.getSuperclass() == ScCachedEntity.class) {
                    ScCachedEntity referencedEntity = (ScCachedEntity)field.get(this);
                    
                    if (referencedEntity != null) {
                        Field keyField = this.getClass().getField(field.getName() + "Key");
                        keyField.set(this, new Key<T>((Class<T>)classOfField, referencedEntity.entityId));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }
    
    
    public boolean isSharedEntity()
    {
        boolean isSharedEntity = false;
        
        isSharedEntity = isSharedEntity || this.getClass().equals(ScDevice.class);
        isSharedEntity = isSharedEntity || this.getClass().equals(ScDeviceListing.class);
        isSharedEntity = isSharedEntity || this.getClass().equals(ScHousehold.class);
        isSharedEntity = isSharedEntity || this.getClass().equals(ScScolaMember.class);
        
        return isSharedEntity;
    }
    
    
    public boolean isReferenceToSharedEntity()
    {
        return this.getClass().equals(ScSharedEntityRef.class);
    }
}
