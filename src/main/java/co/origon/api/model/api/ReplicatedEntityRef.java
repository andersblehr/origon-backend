package co.origon.api.model.api;

import co.origon.api.model.EntityKey;

public interface ReplicatedEntityRef extends ReplicatedEntity {

  EntityKey referencedEntityKey();

  @Override
  default boolean isEntityRef() {
    return true;
  }
}
