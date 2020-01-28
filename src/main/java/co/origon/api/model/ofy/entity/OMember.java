package co.origon.api.model.ofy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.condition.IfNull;
import java.util.Date;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"entityKey", "parentKey", "proxyKey", "proxyId"},
    ignoreUnknown = true)
public class OMember extends OReplicatedEntity {
  public String name;
  public String gender;
  public @IgnoreSave(IfNull.class) Date dateOfBirth;
  public @IgnoreSave(IfNull.class) String mobilePhone;
  public @IgnoreSave(IfNull.class) String email;
  public @IgnoreSave(IfNull.class) String settings;

  public @IgnoreSave(IfNull.class) boolean isMinor;
  public @IgnoreSave(IfNull.class) String fatherId;
  public @IgnoreSave(IfNull.class) String motherId;

  public @IgnoreSave(IfNull.class) String createdIn;
  public @IgnoreSave(IfNull.class) Date activeSince;

  public OMember() {
    super();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Key<OMember> getEntityKey() {
    return Key.create(Key.create(OOrigo.class, "~" + entityId), OMember.class, entityId);
  }

  public Key<OMemberProxy> getProxyKey() {
    return Key.create(OMemberProxy.class, getProxyId());
  }

  public String getProxyId() {
    return hasEmail() ? email : entityId;
  }

  public boolean hasEmail() {
    return email != null && email.length() > 0;
  }
}
