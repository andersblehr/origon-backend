package co.origon.api.model;

public class EntityKey {

  private final String entityId;
  private final String parentId;

  public static EntityKey from(String entityId) {
    return EntityKey.from(entityId, null);
  }

  public static EntityKey from(String entityId, String parentId) {
    return new EntityKey(entityId, parentId);
  }

  public String entityId() {
    return entityId;
  }

  public String parentId() {
    return parentId;
  }

  private EntityKey(String entityId, String parentId) {
    this.entityId = entityId;
    this.parentId = parentId;
  }
}
