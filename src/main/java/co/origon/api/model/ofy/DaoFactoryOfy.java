package co.origon.api.model.ofy;

import co.origon.api.common.ODao;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.Entity;

public class DaoFactoryOfy implements DaoFactory {

    @Override
    public <E extends Entity<E>> Dao<E> daoFor(Class<E> c) {
        return new DaoOfy<>(c);
    }

    // TODO: Factor this into overall DAO architecture
    @Override
    public ODao legacyDao() {
        return new ODao();
    }
}
