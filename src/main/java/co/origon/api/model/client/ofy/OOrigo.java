package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Origo;
import com.googlecode.objectify.annotation.Subclass;
import lombok.NoArgsConstructor;

@Subclass
@NoArgsConstructor
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

  public OOrigo(Origo origo) {
    super(origo);
    name = origo.name();
    type = origo.type();
    descriptionText = origo.descriptionText();
    address = origo.address();
    location = origo.location();
    telephone = origo.telephone();
    permissions = origo.permissions();
    isForMinors = origo.isForMinors();
    joinCode = origo.joinCode();
    internalJoinCode = origo.internalJoinCode();
  }

  @Override
  public Origo fromOfy() {
    return Origo.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
        .name(name)
        .type(type)
        .descriptionText(descriptionText)
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
