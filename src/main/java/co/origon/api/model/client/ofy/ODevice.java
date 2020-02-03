package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Device;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;
import java.util.Date;
import java.util.Map;
import lombok.NoArgsConstructor;

@Subclass
@NoArgsConstructor
public class ODevice extends OReplicatedEntity {
  public String type;
  public String name;
  public Date lastSeen;

  public @Ignore OMember user;
  public @Ignore Map<String, String> userRef;
  public Key<OMember> userKey;

  public ODevice(Device device) {
    super(device);
    type = device.type();
    name = device.name();
    lastSeen = device.lastSeen();
  }

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
