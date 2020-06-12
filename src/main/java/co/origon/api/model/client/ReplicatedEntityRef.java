package co.origon.api.model.client;

import co.origon.api.model.EntityKey;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "referencedEntityKey", "expired"},
    ignoreUnknown = true)
public class ReplicatedEntityRef extends ReplicatedEntity {

  String referencedEntityId;
  String referencedEntityOrigoId;

  @Override
  public boolean isEntityRef() {
    return true;
  }

  @JsonIgnore
  public EntityKey referencedEntityKey() {
    return EntityKey.from(referencedEntityId(), referencedEntityOrigoId());
  }
}
