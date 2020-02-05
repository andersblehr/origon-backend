package co.origon.api.model.client;

import co.origon.api.model.EntityKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Date;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Accessors(fluent = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityClass")
@JsonSubTypes({
  @Type(value = Device.class, name = "ODevice"),
  @Type(value = Member.class, name = "OMember"),
  @Type(value = Membership.class, name = "OMembership"),
  @Type(value = Origo.class, name = "OOrigo"),
  @Type(value = ReplicatedEntityRef.class, name = "OReplicatedEntityRef")
})
public abstract class ReplicatedEntity {

  private final String entityId;
  private final String origoId;
  private final String entityClass;
  private final String createdBy;
  private final Date dateCreated;
  private final String modifiedBy;
  private final Date dateReplicated;
  private final boolean isExpired;

  public EntityKey entityKey() {
    return EntityKey.from(entityId, origoId);
  }

  public EntityKey origoKey() {
    return EntityKey.from(origoId, origoId);
  }

  @JsonIgnore
  public boolean isPersisted() {
    return dateReplicated != null;
  }

  @JsonIgnore
  public boolean isEntityRef() {
    return false;
  }

  @JsonIgnore
  public boolean isMember() {
    return false;
  }

  @JsonIgnore
  public boolean isMembership() {
    return false;
  }

  @Override
  public boolean equals(Object other) {
    return ReplicatedEntity.class.isAssignableFrom(other.getClass())
        && other.hashCode() == this.hashCode();
  }

  @Override
  public int hashCode() {
    return String.format("%s$%s", origoId, entityId).hashCode();
  }
}
