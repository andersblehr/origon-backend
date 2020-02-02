package co.origon.api.model.ofy;

import co.origon.api.model.client.Device;
import co.origon.api.repository.ofy.OfyMapper;
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
  private String type;
  private String name;
  private Date lastSeen;

  private @Ignore OMember user;
  private @Ignore Map<String, String> userRef;
  private Key<OMember> userKey;

  @Override
  public Device fromOfy() {
    return null;
  }
}
