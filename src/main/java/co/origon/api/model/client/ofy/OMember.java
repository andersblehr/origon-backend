package co.origon.api.model.client.ofy;

import co.origon.api.model.client.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.annotation.Subclass;
import java.util.Date;
import lombok.NoArgsConstructor;

@Subclass
@NoArgsConstructor
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"entityKey", "parentKey", "proxyKey", "proxyId"},
    ignoreUnknown = true)
public class OMember extends OReplicatedEntity {
  public String name;
  public String gender;
  public Date dateOfBirth;
  public String mobilePhone;
  public String email;
  public String motherId;
  public String fatherId;
  public boolean isMinor;
  public String createdIn;
  public Date activeSince;
  public String settings;

  public OMember(Member member) {
    super(member);
    name = member.name();
    gender = member.gender();
    dateOfBirth = member.dateOfBirth();
    mobilePhone = member.mobilePhone();
    email = member.email();
    motherId = member.motherId();
    fatherId = member.fatherId();
    isMinor = member.isMinor();
    createdIn = member.createdIn();
    activeSince = member.activeSince();
    settings = member.settings();
  }

  @Override
  public Member fromOfy() {
    return Member.builder()
        .entityId(entityId)
        .origoId(origoId)
        .entityClass(entityClass)
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
        .dateCreated(dateCreated)
        .modifiedBy(modifiedBy)
        .dateReplicated(dateReplicated)
        .isExpired(isExpired)
        .build();
  }
}
