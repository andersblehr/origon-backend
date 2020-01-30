package co.origon.api.model.api.entity;

public interface Origo extends ReplicatedEntity {

  String name();

  String type();

  String description();

  String address();

  String location();

  String telephone();

  String permissions();

  boolean isForMinors();

  String joinCode();

  String internalJoinCode();

  default boolean isResidence() {
    return type() != null && type().equals("residence");
  }

  default boolean isPrivate() {
    return type() != null && type().equals("private");
  }

  default boolean takesPrecedenceOver(Origo other) {
    if (other == null) {
      return true;
    }
    if (type() == null || type().equals("~")) {
      return false;
    }
    if (!other.isResidence()) {
      return !isPrivate();
    }
    return false;
  }
}
