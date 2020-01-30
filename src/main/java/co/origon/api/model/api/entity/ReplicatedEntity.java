package co.origon.api.model.api.entity;

import co.origon.api.model.EntityKey;
import java.time.Instant;

public interface ReplicatedEntity extends Entity<ReplicatedEntity> {

  String entityClass();

  String parentId();

  String createdBy();

  Instant createdAt();

  String modifiedBy();

  Instant modifiedAt();

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
