package co.origon.api.model.client;

import co.origon.api.model.ReplicatedEntity;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@Getter()
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class Member extends ReplicatedEntity {

  private final String name;
  private final String gender;
  private final Date dateOfBirth;
  private final String mobilePhone;
  private final String email;
  private final String motherId;
  private final String fatherId;
  private final boolean isMinor;
  private final String createdIn;
  private final Date activeSince;
  private final String settings;

  @Override
  public String parentId() {
    return "~" + id();
  };

  @Override
  public boolean isMember() {
    return true;
  }

  public String proxyId() {
    return hasEmail() ? email : id();
  }

  public boolean hasEmail() {
    return email() != null && email().length() > 0;
  }
}
