package co.origon.api.model.ofy;

import co.origon.api.model.client.Member;
import co.origon.api.repository.ofy.OfyMapper;
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
    return null;
  }
}
