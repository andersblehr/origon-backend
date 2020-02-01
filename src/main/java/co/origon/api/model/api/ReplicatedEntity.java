package co.origon.api.model.api;

import co.origon.api.model.EntityKey;
import java.util.Date;

public interface ReplicatedEntity extends Entity<ReplicatedEntity> {

  String entityClass();

  String parentId();

  String createdBy();

  Date createdAt();

  String modifiedBy();

  Date modifiedAt();

  boolean isExpired();

  boolean isEntityRef();

  boolean isMembership();

  boolean isMember();

  @Override
  default EntityKey key() {
    return EntityKey.from(id(), parentId());
  }

  default EntityKey parentKey() {
    return EntityKey.from(parentId());
  }

  default boolean isPersisted() {
    return modifiedAt() != null;
  }
}
