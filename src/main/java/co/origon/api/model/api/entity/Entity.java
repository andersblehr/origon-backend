package co.origon.api.model.api.entity;

import co.origon.api.model.EntityKey;

public interface Entity<E extends Entity<E>> {
  String id();

  default EntityKey key() {
    return EntityKey.from(id());
  }
}
