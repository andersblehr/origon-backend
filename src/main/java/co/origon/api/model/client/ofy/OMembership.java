package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Membership;
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
public class OMembership extends OReplicatedEntity {
  private String type;
  private boolean isAdmin = false;
  private String status;
  private String affiliations;

  private @Ignore OMember member;
  private @Ignore Map<String, String> memberRef;
  private Key<OMember> memberKey;

  private @IgnoreSave Map<String, String> origoRef;

  @Override
  public Membership fromOfy() {
    return Membership.builder()
        .id(entityId)
        .parentId(origoId)
        .type(type)
        .isAdmin(isAdmin)
        .status(status)
        .affiliations(affiliations)
        .build();
  }
}
