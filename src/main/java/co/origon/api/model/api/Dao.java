package co.origon.api.model.api;

import java.util.Collection;

public interface Dao<E extends Entity> {
    E create();
    E get(String key);
    void save(E entiy);
    void delete(E entity);

    Collection<E> get(Collection<String> keys);
    void save(Collection<E> entities);
    void delete(Collection<E> entities);

    @SuppressWarnings("unchecked")
    default E produce(String key) {
        E entity = get(key);
        return entity != null ? entity : (E) create().key(key);
    }
}
