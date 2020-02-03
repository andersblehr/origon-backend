package co.origon.api.model.client;

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
@Getter()
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "expired", "forMinors"},
    ignoreUnknown = true)
public class Origo extends ReplicatedEntity {

  private final String name;
  private final String type;
  private final String descriptionText;
  private final String address;
  private final String location;
  private final String telephone;
  private final String permissions;
  private final boolean isForMinors;
  private final String joinCode;
  private final String internalJoinCode;

  @JsonIgnore
  public boolean isResidence() {
    return type() != null && type().equals("residence");
  }

  @JsonIgnore
  public boolean isPrivate() {
    return type() != null && type().equals("private");
  }

  public boolean takesPrecedenceOver(Origo other) {
    if (other == null) {
      return true;
    }
    if (type() == null || type().equals("~")) {
      return false;
    }
    if (!other.isResidence()) {
      return !isPrivate();
    }
    return false;
  }
}
