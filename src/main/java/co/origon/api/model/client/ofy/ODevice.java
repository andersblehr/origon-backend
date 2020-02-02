package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Device;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;
import java.util.Date;
import java.util.Map;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "userKey"},
    ignoreUnknown = true)
public class ODevice extends OReplicatedEntity {
  public String type;
  public String name;
  public Date lastSeen;

  public @Ignore OMember user;
  public @Ignore Map<String, String> userRef;
  public Key<OMember> userKey;

  @Override
  public Device fromOfy() {
    return Device.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
        .type(type)
        .name(name)
        .lastSeen(lastSeen)
        .userRef(userRef)
        .createdBy(createdBy)
        .dateCreated(dateCreated)
        .modifiedBy(modifiedBy)
        .dateReplicated(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
