package co.origon.api.model.ofy;

import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.Entity;

public class DaoFactoryOfy implements DaoFactory {

    @Override
    public <E extends Entity> Dao<E> daoFor(Class<E> c) {
        return new DaoOfy<>(c);
    }
}
