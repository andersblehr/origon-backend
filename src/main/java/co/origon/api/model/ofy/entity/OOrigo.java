package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.Origo;
import co.origon.api.model.api.entity.ReplicatedEntity;
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

public class OOrigo extends OReplicatedEntity implements Origo {
  private String name;
  private String type;

  private String descriptionText;
  private String address;
  private String location;
  private String telephone;
  private String permissions;
  private boolean isForMinors;

  private String joinCode;
  private @Index(IfNotNull.class) String internalJoinCode;

  public OOrigo() {
    super();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String type() {
    return type;
  }

  @Override
  public String description() {
    return descriptionText;
  }

  @Override
  public String address() {
    return address;
  }

  @Override
  public String location() {
    return location;
  }

  @Override
  public String telephone() {
    return telephone;
  }

  @Override
  public String permissions() {
    return permissions;
  }

  @Override
  public boolean isForMinors() {
    return isForMinors;
  }

  @Override
  public String joinCode() {
    return joinCode;
  }

  @Override
  public String internalJoinCode() {
    return internalJoinCode;
  }
}
