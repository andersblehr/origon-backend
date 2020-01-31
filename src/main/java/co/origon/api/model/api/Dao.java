package co.origon.api.model.api;

import co.origon.api.model.api.entity.Entity;
import java.util.Collection;

public interface Dao<E extends Entity<E>> {
  E create();

  E get(String key);

  void save(E entiy);

  void delete(E entity);

  Collection<E> get(Collection<String> keys);

  void save(Collection<E> entities);

  void delete(Collection<E> entities);
}
