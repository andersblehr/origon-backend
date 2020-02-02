package co.origon.api.model.client;

import co.origon.api.model.EntityKey;
import co.origon.api.model.ReplicatedEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ReplicatedEntityRef extends ReplicatedEntity {

  private final String referencedEntityId;
  private final String referencedEntityParentId;

  @Override
  public boolean isEntityRef() {
    return true;
  }

  public EntityKey referencedEntityKey() {
    return EntityKey.from(referencedEntityId(), referencedEntityParentId());
  }
}
