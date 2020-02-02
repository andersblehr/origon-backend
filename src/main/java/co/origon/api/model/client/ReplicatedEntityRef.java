package co.origon.api.model.client;

import co.origon.api.model.EntityKey;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ReplicatedEntityRef extends ReplicatedEntity {

  private final String referencedEntityId;
  private final String referencedEntityParentId;

  @Override
  public boolean isEntityRef() {
    return true;
  }

  @JsonIgnore
  public EntityKey referencedEntityKey() {
    return EntityKey.from(referencedEntityId(), referencedEntityParentId());
  }
}
