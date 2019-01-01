package co.origon.api.model.ofy;

import co.origon.api.model.api.Dao;
import co.origon.api.model.api.Entity;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMemberProxy;
import com.googlecode.objectify.Key;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DaoOfy<E extends Entity> implements Dao<E> {

    private Class<E> clazz;

    DaoOfy(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E create() {
        if (clazz.equals(MemberProxy.class))
            return (E) new OMemberProxy();
        if (clazz.equals(DeviceCredentials.class))
            return (E) new OAuthMeta();

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(String key) {
        if (clazz.equals(Config.class))
            return (E) ofy().load().type(co.origon.api.common.Config.class).id(key).now();
        if (clazz.equals(DeviceCredentials.class))
            return (E) ofy().load().type(OAuthMeta.class).id(key).now();
        if (clazz.equals(MemberProxy.class))
            return (E) ofy().load().type(OMemberProxy.class).id(key).now();

        return null;
    }

    @Override
    public void save(E entity) {
        ofy().save().entity(entity).now();
    }

    @Override
    public void delete(E entity) {
        ofy().delete().entity(entity);
    }

    @Override
    public Collection<E> get(Collection<String> keys) {
        return ofy()
                .load()
                .keys(keys.stream()
                        .map(key -> Key.create(clazz, key))
                        .collect(Collectors.toSet())
                ).values();
    }

    @Override
    public void save(Collection<E> entities) {
        ofy().save().entities(entities).now();
    }

    @Override
    public void delete(Collection<E> entities) {
        ofy().delete().entities(entities).now();
    }
}
