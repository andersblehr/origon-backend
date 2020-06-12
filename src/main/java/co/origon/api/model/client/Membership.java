package co.origon.api.model.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Arrays;
import java.util.Map;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"parentKey", "memberKey", "expired", "admin"},
    ignoreUnknown = true)
public class Membership extends ReplicatedEntity {

  String type;
  boolean isAdmin;
  String status;
  String affiliations;
  Member member;
  Map<String, String> memberRef;
  Map<String, String> origoRef;

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
    return dateReplicated() == null
        && member().hasEmail()
        && type() != null
        && Arrays.asList("P", "R", "L", "A").contains(type())
        && ((status() == null && isAssociate())
            || (status() != null && (!status().equals("A") || type().equals("R"))));
  }
}
