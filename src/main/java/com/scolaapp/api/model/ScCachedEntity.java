package com.scolaapp.api.model;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
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
    @Type(value = ScMember.class, name = "ScMember"),
    @Type(value = ScMemberResidency.class, name = "ScMemberResidency"),
    @Type(value = ScMembership.class, name = "ScMembership"),
    @Type(value = ScMessageBoard.class, name = "ScMessageBoard"),
    @Type(value = ScScola.class, name = "ScScola"),
    @Type(value = ScSharedEntityRef.class, name = "ScSharedEntityRef")})
public abstract class ScCachedEntity
{
    public @Id String entityId;
    public @NotSaved String entityClass;
    
    public Date dateCreated;
    public @NotSaved(IfNull.class) @Indexed(IfNotNull.class) Date dateModified;
    public @NotSaved(IfNull.class) Date dateExpires;
    
    public @NotSaved(IfDefault.class) boolean isShared = false;
    
    public @NotSaved(IfNull.class) @Indexed(IfNotNull.class) Key<ScScola> scolaKey;
    public @NotSaved(IfNull.class) String scolaId;

    
    public ScCachedEntity() {}
    
    
    private Map<String, String> createEntityRef(String entityClass, String entityId)
    {
        Map<String, String> entityRef = new HashMap<String, String>();
        
        entityRef.put("entityClass", entityClass);
        entityRef.put("entityId", entityId);
        
        return entityRef;
    }
    
    
    @SuppressWarnings("unchecked")
    public <T extends ScCachedEntity> void internaliseRelationships()
    {
        try {
            if (scolaId != null) {
                scolaKey = new Key<ScScola>(ScScola.class, scolaId);
            }
            
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
    
    
    @SuppressWarnings("unchecked")
    public <T extends ScCachedEntity> void externaliseRelationships()
    {
        try {
            if (scolaKey != null) {
                scolaId = scolaKey.getRaw().getName();
            }
            
            Field[] fields = this.getClass().getFields();
            
            for (Field field : fields) {
                Class<?> classOfField = field.getType();
                
                if (classOfField.getSuperclass() == ScCachedEntity.class) {
                    Field keyField = this.getClass().getField(field.getName() + "Key");
                    Key<T> referencedEntityKey = (Key<T>)keyField.get(this);
                    
                    if (referencedEntityKey != null) {
                        String referencedEntityClassName = classOfField.getSimpleName();
                        String referencedEntityId = referencedEntityKey.getRaw().getName();
                        
                        Field refField = this.getClass().getField(field.getName() + "Ref");
                        refField.set(this, createEntityRef(referencedEntityClassName, referencedEntityId));
                    }
                } else if (field.getName().equals("scolaRef")) {
                    field.set(this, createEntityRef("ScScola", scolaId));
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
}
