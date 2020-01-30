package co.origon.api.model.api.entity;

import java.time.Instant;

public interface Member extends ReplicatedEntity {

  String name();

  String gender();

  Instant dateOfBirth();

  String mobilePhone();

  String email();

  String motherId();

  String fatherId();

  boolean isMinor();

  String createdIn();

  Instant activeSince();

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
