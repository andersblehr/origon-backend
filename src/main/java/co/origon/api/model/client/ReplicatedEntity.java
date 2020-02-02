package co.origon.api.model.client;

import co.origon.api.model.Entity;
import co.origon.api.model.EntityKey;
import java.util.Date;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Accessors(fluent = true)
public abstract class ReplicatedEntity extends Entity {

  private final String parentId;
  private final String createdBy;
  private final Date createdAt;
  private final String modifiedBy;
  private final Date modifiedAt;
  private final boolean isExpired;

  @Override
  public EntityKey key() {
    return EntityKey.from(id(), parentId);
  }

  public EntityKey parentKey() {
    return EntityKey.from(parentId);
  }

  public boolean isPersisted() {
    return modifiedAt != null;
  }

  public boolean isEntityRef() {
    return false;
  }

  public boolean isMember() {
    return false;
  }

  public boolean isMembership() {
    return false;
  }
}
