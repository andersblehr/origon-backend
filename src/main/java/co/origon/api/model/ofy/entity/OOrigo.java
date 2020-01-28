package co.origon.api.model.ofy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNotNull;
import com.googlecode.objectify.condition.IfNull;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey"},
    ignoreUnknown = true)
public class OOrigo extends OReplicatedEntity {
  public @IgnoreSave(IfNull.class) String name;
  public String type;

  public @IgnoreSave(IfNull.class) String descriptionText;
  public @IgnoreSave(IfNull.class) String address;
  public @IgnoreSave(IfNull.class) String location;
  public @IgnoreSave(IfNull.class) String telephone;
  public @IgnoreSave(IfNull.class) String permissions;
  public @IgnoreSave(IfFalse.class) boolean isForMinors;

  public @IgnoreSave(IfNull.class) String joinCode;
  public @Index(IfNotNull.class) @IgnoreSave(IfNull.class) String internalJoinCode;

  public OOrigo() {
    super();
  }

  public boolean takesPrecedenceOver(OOrigo other) {
    if (other == null) {
      return true;
    }
    if (type == null || type.equals("~")) {
      return false;
    }
    if (!other.isResidence()) {
      return !isPrivate();
    }
    return false;
  }

  @JsonIgnore
  public boolean isResidence() {
    return type != null && type.equals("residence");
  }

  @JsonIgnore
  public boolean isPrivate() {
    return type != null && type.equals("private");
  }
}
