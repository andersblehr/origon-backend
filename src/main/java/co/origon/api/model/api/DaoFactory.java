package co.origon.api.model.api;

import co.origon.api.model.api.entity.Entity;

public interface DaoFactory {
  <E extends Entity<E>> Dao<E> daoFor(Class<E> c);
}
