package co.origon.api.model.ofy;

import co.origon.api.model.api.ReplicatedEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityClass")
@JsonSubTypes({
  @Type(value = ODevice.class, name = "ODevice"),
  @Type(value = OMember.class, name = "OMember"),
  @Type(value = OMembership.class, name = "OMembership"),
  @Type(value = OOrigo.class, name = "OOrigo"),
  @Type(value = OReplicatedEntityRef.class, name = "OReplicatedEntityRef")
})
public class OReplicatedEntity implements ReplicatedEntity {
  protected @Parent Key<OOrigo> parentKey;
  protected @Id String entityId;

  protected @Ignore String origoId;
  protected @Ignore String entityClass;
  protected boolean isExpired;

  protected String createdBy;
  protected String modifiedBy;
  protected Date dateCreated;
  protected @Index Date dateReplicated;

  @OnSave
  @SuppressWarnings("unchecked")
  public <T extends OReplicatedEntity> void internaliseRelationships() {
    try {
      dateReplicated = new Date();
      parentKey = Key.create(OOrigo.class, origoId);

      Field[] fields = this.getClass().getFields();

      for (Field field : fields) {
        Class<?> classOfField = field.getType();

        if (OReplicatedEntity.class.isAssignableFrom(classOfField)) {
          OReplicatedEntity referencedEntity = (OReplicatedEntity) field.get(this);

          if (referencedEntity != null) {
            Field keyField = this.getClass().getField(field.getName() + "Key");
            keyField.set(
                this, Key.create(parentKey, (Class<T>) classOfField, referencedEntity.entityId));
          }
        }
      }
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @OnLoad
  @SuppressWarnings("unchecked")
  public <T extends OReplicatedEntity> void externaliseRelationships() {
    try {
      origoId = parentKey.getRaw().getName();

      Field origoRefField = getOrigoRefField();

      if (origoRefField != null) {
        origoRefField.set(this, createEntityRef("OOrigo", origoId));
      }

      Field[] fields = this.getClass().getFields();

      for (Field field : fields) {
        Class<?> classOfField = field.getType();

        if (OReplicatedEntity.class.isAssignableFrom(classOfField)) {
          Field keyField = this.getClass().getField(field.getName() + "Key");
          Key<T> referencedEntityKey = (Key<T>) keyField.get(this);

          if (referencedEntityKey != null) {
            String referencedEntityClassName = classOfField.getSimpleName();
            String referencedEntityId = referencedEntityKey.getRaw().getName();

            Field refField = this.getClass().getField(field.getName() + "Ref");
            refField.set(this, createEntityRef(referencedEntityClassName, referencedEntityId));
          }
        }
      }

      entityClass = this.getClass().getSimpleName();
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object other) {
    boolean areEqual = false;

    if (OReplicatedEntity.class.isAssignableFrom(other.getClass())) {
      areEqual = (other.hashCode() == this.hashCode());
    }

    return areEqual;
  }

  @Override
  public int hashCode() {
    return String.format("%s$%s", origoId, entityId).hashCode();
  }

  @Override
  public String entityClass() {
    return entityClass;
  }

  @Override
  public String id() {
    return entityId;
  }

  @Override
  public String parentId() {
    return origoId;
  }

  @Override
  public String createdBy() {
    return createdBy;
  }

  @Override
  public Date createdAt() {
    return dateCreated;
  }

  @Override
  public String modifiedBy() {
    return modifiedBy;
  }

  @Override
  public Date modifiedAt() {
    return dateReplicated;
  }

  @Override
  public boolean isExpired() {
    return isExpired;
  }

  @Override
  public boolean isEntityRef() {
    return false;
  }

  @Override
  public boolean isMembership() {
    return false;
  }

  @Override
  public boolean isMember() {
    return false;
  }

  private Field getOrigoRefField() {
    try {
      return this.getClass().getField("origoRef");
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  private Map<String, String> createEntityRef(String entityClass, String entityId) {
    Map<String, String> entityRef = new HashMap<>();

    entityRef.put("entityClass", entityClass);
    entityRef.put("entityId", entityId);

    return entityRef;
  }
}
