package co.origon.api.model.ofy.entity;

import co.origon.api.model.api.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.annotation.Subclass;
import java.time.Instant;
import java.util.Date;

@Subclass
@JsonSerialize
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"entityKey", "parentKey", "proxyKey", "proxyId"},
    ignoreUnknown = true)
public class OMember extends OReplicatedEntity implements Member {
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

  public OMember() {
    super();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String gender() {
    return gender;
  }

  @Override
  public Instant dateOfBirth() {
    return dateOfBirth.toInstant();
  }

  @Override
  public String mobilePhone() {
    return mobilePhone;
  }

  @Override
  public String email() {
    return email;
  }

  @Override
  public String motherId() {
    return motherId;
  }

  @Override
  public String fatherId() {
    return fatherId;
  }

  @Override
  public boolean isMinor() {
    return isMinor;
  }

  @Override
  public String createdIn() {
    return createdIn;
  }

  @Override
  public Instant activeSince() {
    return activeSince.toInstant();
  }
}
