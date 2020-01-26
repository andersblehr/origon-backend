package co.origon.api.model.api;

public interface DaoFactory {
  <E extends Entity<E>> Dao<E> daoFor(Class<E> c);
}
