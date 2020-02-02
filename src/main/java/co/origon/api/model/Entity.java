package co.origon.api.model;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Accessors(fluent = true)
public abstract class Entity {

  private final String id;

  public EntityKey key() {
    return EntityKey.from(id);
  }
}
