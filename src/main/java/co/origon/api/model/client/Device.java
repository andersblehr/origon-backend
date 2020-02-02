package co.origon.api.model.client;

import co.origon.api.model.ReplicatedEntity;
import java.util.Date;
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
public class Device extends ReplicatedEntity {

  private final String type;
  private final String name;
  private final Date lastSeen;
  private final Member user;
}
