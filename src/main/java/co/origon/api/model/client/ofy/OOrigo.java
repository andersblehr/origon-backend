package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Origo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.annotation.Subclass;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey"},
    ignoreUnknown = true)
public class OOrigo extends OReplicatedEntity {
  public String name;
  public String type;

  public String descriptionText;
  public String address;
  public String location;
  public String telephone;
  public String permissions;
  public boolean isForMinors;

  public String joinCode;
  public String internalJoinCode;

  @Override
  public Origo fromOfy() {
    return Origo.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
        .name(name)
        .type(type)
        .description(descriptionText)
        .address(address)
        .location(location)
        .telephone(telephone)
        .permissions(permissions)
        .isForMinors(isForMinors)
        .joinCode(joinCode)
        .internalJoinCode(internalJoinCode)
        .createdBy(createdBy)
        .dateCreated(dateCreated)
        .modifiedBy(modifiedBy)
        .dateReplicated(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
