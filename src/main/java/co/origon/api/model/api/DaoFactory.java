package co.origon.api.model.api;

public interface DaoFactory {
    <E extends Entity> Dao<E> daoFor(Class<E> c);
}
