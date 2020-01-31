package co.origon.api.repository.api;

import co.origon.api.model.EntityKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface Repository<T> {

  Optional<T> getById(String id);

  Optional<T> getByKey(EntityKey key);

  Collection<T> getByIds(Collection<String> ids);

  Collection<T> getByKeys(Collection<EntityKey> keys);

  Collection<T> getByParentId(String parentId);

  Collection<T> getByParentId(String parentId, Instant modifiedAfter);

  Collection<T> getByPropertyValue(String property, Object value);

  void save(T entity);

  void save(Collection<T> entities);

  void deleteById(String id);

  void deleteByKey(EntityKey key);

  void deleteByIds(Collection<String> ids);

  void deleteByKeys(Collection<EntityKey> keys);
}
