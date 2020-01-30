package co.origon.api.model.api.entity;

import com.sun.tools.javac.util.List;

public interface Membership extends ReplicatedEntity {

  String type();

  String status();

  String affiliations();

  Member member();

  boolean isAdmin();

  @Override
  default boolean isMembership() {
    return true;
  }

  default boolean isAssociate() {
    return type().equals("A");
  }

  default boolean isFetchable() {
    if (isExpired() || type().equals("F")) {
      return false;
    }
    if (type().equals("~") || isAssociate()) {
      return true;
    }
    return status() != null && !status().equals("-");
  }

  default boolean isInvitable() {
    return modifiedAt() == null
        && member().hasEmail()
        && type() != null
        && List.of("P", "R", "L", "A").contains(type())
        && ((status() == null && isAssociate())
            || (status() != null && (!status().equals("A") || type().equals("R"))));
  }
}
