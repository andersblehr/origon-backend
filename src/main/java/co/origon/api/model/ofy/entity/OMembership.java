package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.Member;
import co.origon.api.model.api.entity.Membership;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Subclass;
import java.util.Map;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "memberKey"},
    ignoreUnknown = true)
public class OMembership extends OReplicatedEntity implements Membership {
  private String type;
  private boolean isAdmin = false;
  private String status;
  private String affiliations;

  private @Ignore OMember member;
  private @Ignore Map<String, String> memberRef;
  private Key<OMember> memberKey;

  private @IgnoreSave Map<String, String> origoRef;

  private OMembership() {
    super();
  }

  @JsonIgnore
  public Key<OOrigo> getOrigoKey() {
    if (parentKey == null) {
      parentKey = Key.create(OOrigo.class, origoId);
    }
    return Key.create(parentKey, OOrigo.class, origoId);
  }

  @Override
  public String type() {
    return type;
  }

  @Override
  public String status() {
    return status;
  }

  @Override
  public String affiliations() {
    return affiliations;
  }

  @Override
  public Member member() {
    return member;
  }

  @Override
  public boolean isAdmin() {
    return isAdmin;
  }
}
