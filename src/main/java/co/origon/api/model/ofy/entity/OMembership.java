package co.origon.api.model.ofy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.condition.IfFalse;
import com.googlecode.objectify.condition.IfNull;
import java.util.Map;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "memberKey"},
    ignoreUnknown = true)
public class OMembership extends OReplicatedEntity {
  public String type;
  public @IgnoreSave(IfFalse.class) boolean isAdmin = false;
  public @IgnoreSave(IfNull.class) String status;
  public @IgnoreSave(IfNull.class) String affiliations;

  public @Ignore OMember member;
  public @Ignore Map<String, String> memberRef;
  public Key<OMember> memberKey;

  public @IgnoreSave Map<String, String> origoRef;

  public OMembership() {
    super();
  }

  @JsonIgnore
  public Key<OOrigo> getOrigoKey() {
    if (parentKey == null) {
      parentKey = Key.create(OOrigo.class, origoId);
    }
    return Key.create(parentKey, OOrigo.class, origoId);
  }

  @JsonIgnore
  public boolean isAssociate() {
    return type.equals("A");
  }

  @JsonIgnore
  public boolean isFetchable() {
    boolean isFetchable = false;

    if (!isExpired && !type.equals("F")) {
      isFetchable = type.equals("~") || type.equals("A") || (status != null && !status.equals("-"));
    }

    return isFetchable;
  }

  @JsonIgnore
  public boolean isInvitable() {
    boolean isInvitable = dateReplicated == null;
    isInvitable = isInvitable && member.hasEmail();
    isInvitable =
        isInvitable
            && type != null
            && (type.equals("P") || type.equals("R") || type.equals("L") || type.equals("A"));
    isInvitable =
        isInvitable
            && ((status == null && type.equals("A"))
                || (status != null && (!status.equals("A") || type.equals("R"))));

    return isInvitable;
  }
}
