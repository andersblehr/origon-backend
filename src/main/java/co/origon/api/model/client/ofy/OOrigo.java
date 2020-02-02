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
  private String name;
  private String type;

  private String descriptionText;
  private String address;
  private String location;
  private String telephone;
  private String permissions;
  private boolean isForMinors;

  private String joinCode;
  private String internalJoinCode;

  @Override
  public Origo fromOfy() {
    return Origo.builder()
        .id(entityId)
        .parentId(origoId)
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
        .createdAt(dateCreated)
        .modifiedBy(modifiedBy)
        .modifiedAt(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
