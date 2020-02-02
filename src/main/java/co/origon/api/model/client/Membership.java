package co.origon.api.model.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Membership extends ReplicatedEntity {

  private final String type;
  private final boolean isAdmin;
  private final String status;
  private final String affiliations;
  private final Member member;
  private final Origo origo;

  @Override
  @JsonIgnore
  public boolean isMembership() {
    return true;
  }

  @JsonIgnore
  public boolean isAssociate() {
    return type().equals("A");
  }

  @JsonIgnore
  public boolean isFetchable() {
    if (isExpired() || type().equals("F")) {
      return false;
    }
    if (type().equals("~") || isAssociate()) {
      return true;
    }
    return status() != null && !status().equals("-");
  }

  @JsonIgnore
  public boolean isInvitable() {
    return modifiedAt() == null
        && member().hasEmail()
        && type() != null
        && Arrays.asList("P", "R", "L", "A").contains(type())
        && ((status() == null && isAssociate())
            || (status() != null && (!status().equals("A") || type().equals("R"))));
  }
}
