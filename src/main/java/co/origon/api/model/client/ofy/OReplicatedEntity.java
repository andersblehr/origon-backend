package co.origon.api.model.client.ofy;

import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.repository.ofy.OfyMapper;
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
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityClass")
@JsonSubTypes({
  @Type(value = ODevice.class, name = "ODevice"),
  @Type(value = OMember.class, name = "OMember"),
  @Type(value = OMembership.class, name = "OMembership"),
  @Type(value = OOrigo.class, name = "OOrigo"),
  @Type(value = OReplicatedEntityRef.class, name = "OReplicatedEntityRef")
})
public abstract class OReplicatedEntity implements OfyMapper<ReplicatedEntity> {
  public @Parent Key<OOrigo> parentKey;
  public @Id String entityId;

  public @Ignore String origoId;
  public @Ignore String entityClass;
  public boolean isExpired;

  public String createdBy;
  public Date dateCreated;
  public String modifiedBy;
  public @Index Date dateReplicated;

  public OReplicatedEntity(ReplicatedEntity entity) {
    entityId = entity.entityId();
    origoId = entity.origoId();
    entityClass = entity.entityClass();
    isExpired = entity.isExpired();
    createdBy = entity.createdBy();
    dateCreated = entity.dateCreated();
    modifiedBy = entity.modifiedBy();
    dateReplicated = entity.dateReplicated();
  }

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

  @Override
  public abstract ReplicatedEntity fromOfy();
}
