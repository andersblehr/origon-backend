package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.Device;
import co.origon.api.model.api.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "userKey"},
    ignoreUnknown = true)
public class ODevice extends OReplicatedEntity implements Device {
  private String type;
  private String name;
  private Date lastSeen;

  private @Ignore OMember user;
  private @Ignore Map<String, String> userRef;
  private Key<OMember> userKey;

  public ODevice() {
    super();
  }

  @Override
  public String type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Instant lastSeen() {
    return lastSeen.toInstant();
  }

  @Override
  public Member user() {
    return user;
  }
}
