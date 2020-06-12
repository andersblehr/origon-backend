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

  String name;
  String type;
  String descriptionText;
  String address;
  String location;
  String telephone;
  String permissions;
  boolean isForMinors;
  String joinCode;
  String internalJoinCode;

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
