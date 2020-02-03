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
import lombok.NoArgsConstructor;

@Subclass
@NoArgsConstructor
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "memberKey"},
    ignoreUnknown = true)
public class OMembership extends OReplicatedEntity {
  public String type;
  public boolean isAdmin = false;
  public String status;
  public String affiliations;
  public @Ignore OMember member;
  public @Ignore Map<String, String> memberRef;
  public Key<OMember> memberKey;
  public @IgnoreSave Map<String, String> origoRef;

  public OMembership(Membership membership) {
    super(membership);
    type = membership.type();
    isAdmin = membership.isAdmin();
    status = membership.status();
    affiliations = membership.affiliations();
  }

  @Override
  public Membership fromOfy() {
    return Membership.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
        .type(type)
        .isAdmin(isAdmin)
        .status(status)
        .affiliations(affiliations)
        .member(member != null ? member.fromOfy() : null)
        .memberRef(memberRef)
        .origoRef(origoRef)
        .createdBy(createdBy)
        .dateCreated(dateCreated)
        .modifiedBy(modifiedBy)
        .dateReplicated(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
