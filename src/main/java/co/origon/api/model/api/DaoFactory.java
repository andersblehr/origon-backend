package co.origon.api.model.api;

import co.origon.api.common.ODao;

public interface DaoFactory {
  <E extends Entity<E>> Dao<E> daoFor(Class<E> c);

  // TODO: Factor this into overall DAO architecture
  ODao legacyDao();
}
