package co.origon.api.model.api;

import java.util.Date;

public interface Member extends ReplicatedEntity {

  String name();

  String gender();

  Date dateOfBirth();

  String mobilePhone();

  String email();

  String motherId();

  String fatherId();

  boolean isMinor();

  String createdIn();

  Date activeSince();

  @Override
  default String parentId() {
    return "~" + id();
  };

  @Override
  default boolean isMember() {
    return true;
  }

  default String proxyId() {
    return hasEmail() ? email() : id();
  }

  default boolean hasEmail() {
    return email() != null && email().length() > 0;
  }
}
