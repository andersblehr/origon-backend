package co.origon.api.repository.ofy;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.EntityKey;
import co.origon.api.model.ofy.OOrigo;
import co.origon.api.repository.api.Repository;
import com.googlecode.objectify.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Singleton;

@Singleton
public class RepositoryOfy<T> implements Repository<T> {

  private Class<? extends T> clazz;

  public RepositoryOfy(Class<? extends T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Optional<T> getById(String id) {
    return Optional.ofNullable(ofy().load().key(ofyKeyFromId(id)).now());
  }

  @Override
  public Optional<T> getByKey(EntityKey key) {
    return Optional.ofNullable(ofy().load().key(ofyKeyFromKey(key)).now());
  }

  @Override
  public Collection<T> getByIds(Collection<String> ids) {
    return ofy().load().keys(ofyKeysFromIds(ids)).values();
  }

  @Override
  public Collection<T> getByKeys(Collection<EntityKey> keys) {
    return ofy()
        .load()
        .keys(keys.stream().map(this::ofyKeyFromKey).collect(Collectors.toSet()))
        .values();
  }

  @Override
  public Collection<T> getByParentId(String parentId) {
    return getByParentId(parentId, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<T> getByParentId(String parentId, Date modifiedAfter) {
    return (Collection<T>)
        ofy()
            .load()
            .type(clazz)
            .ancestor(Key.create(OOrigo.class, parentId))
            .filter("dateReplicated >", modifiedAfter != null ? modifiedAfter : new Date(0L))
            .list();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<T> getByPropertyValue(String property, Object value) {
    return (Collection<T>) ofy().load().type(clazz).filter(property, value).list();
  }

  @Override
  public T save(T entity) {
    ofy().save().entity(entity).now();
    return entity;
  }

  @Override
  public void save(Collection<T> entities) {
    ofy().save().entities(entities);
  }

  @Override
  public void deleteById(String id) {
    deleteByIds(Collections.singletonList(id));
  }

  @Override
  public void deleteByKey(EntityKey key) {
    deleteByKeys(Collections.singletonList(key));
  }

  @Override
  public void deleteByIds(Collection<String> ids) {
    ofy().delete().keys(ofyKeysFromIds(ids));
  }

  @Override
  public void deleteByKeys(Collection<EntityKey> keys) {
    ofy().delete().keys(ofyKeysFromKeys(keys));
  }

  private Key<T> ofyKeyFromId(String id) {
    return Key.create(clazz, id);
  }

  private Key<T> ofyKeyFromKey(EntityKey entityKey) {
    return entityKey.parentId() != null
        ? Key.create(Key.create(OOrigo.class, entityKey.parentId()), clazz, entityKey.entityId())
        : ofyKeyFromId(entityKey.entityId());
  }

  private Collection<Key<T>> ofyKeysFromIds(Collection<String> ids) {
    return ids.stream().map(this::ofyKeyFromId).collect(Collectors.toSet());
  }

  private Collection<Key<T>> ofyKeysFromKeys(Collection<EntityKey> keys) {
    return keys.stream().map(this::ofyKeyFromKey).collect(Collectors.toSet());
  }
}
