package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.annotation.Subclass;
import java.util.Date;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"entityKey", "parentKey", "proxyKey", "proxyId"},
    ignoreUnknown = true)
public class OMember extends OReplicatedEntity {
  private String name;
  private String gender;
  private Date dateOfBirth;
  private String mobilePhone;
  private String email;

  private String motherId;
  private String fatherId;
  private boolean isMinor;

  private String createdIn;
  private Date activeSince;

  private String settings;

  @Override
  public Member fromOfy() {
    return Member.builder()
        .id(entityId)
        .parentId(origoId)
        .name(name)
        .gender(gender)
        .dateOfBirth(dateOfBirth)
        .mobilePhone(mobilePhone)
        .email(email)
        .motherId(motherId)
        .fatherId(fatherId)
        .isMinor(isMinor)
        .createdIn(createdIn)
        .activeSince(activeSince)
        .settings(settings)
        .createdBy(createdBy)
        .createdAt(dateCreated)
        .modifiedBy(modifiedBy)
        .modifiedAt(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
