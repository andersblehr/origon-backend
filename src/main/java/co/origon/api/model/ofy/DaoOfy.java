package co.origon.api.model.ofy;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.common.Config;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.Entity;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.model.api.entity.OneTimeCredentials;
import co.origon.api.model.ofy.entity.OAuthInfo;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMemberProxy;
import com.googlecode.objectify.Key;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.stream.Collectors;

public class DaoOfy<E extends Entity<E>> implements Dao<E> {

  private Class<E> clazz;

  DaoOfy(Class<E> clazz) {
    if (ofyClass(clazz) == null)
      throw new RuntimeException("Unsupported Ofy class: " + clazz.getName());
    this.clazz = clazz;
  }

  @Override
  @SuppressWarnings("unchecked")
  public E create() {
    try {
      return (E) ofyClass().getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      throw new RuntimeException("Error instantiating Ofy class: " + clazz.getName(), e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public E get(String key) {
    return (E) ofy().load().type(ofyClass()).id(key).now();
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
        .keys(keys.stream().map(key -> Key.create(clazz, key)).collect(Collectors.toSet()))
        .values();
  }

  @Override
  public void save(Collection<E> entities) {
    ofy().save().entities(entities).now();
  }

  @Override
  public void delete(Collection<E> entities) {
    ofy().delete().entities(entities).now();
  }

  private Class ofyClass() {
    return ofyClass(clazz);
  }

  private Class ofyClass(Class clazz) {
    if (clazz.equals(co.origon.api.model.api.entity.Config.class)) return Config.class;
    if (clazz.equals(DeviceCredentials.class)) return OAuthMeta.class;
    if (clazz.equals(MemberProxy.class)) return OMemberProxy.class;
    if (clazz.equals(OneTimeCredentials.class)) return OAuthInfo.class;

    return null;
  }
}
