package co.origon.api.model.api;

import co.origon.api.model.EntityKey;

public interface Entity<E> {
  String id();

  default EntityKey key() {
    return EntityKey.from(id());
  }
}
