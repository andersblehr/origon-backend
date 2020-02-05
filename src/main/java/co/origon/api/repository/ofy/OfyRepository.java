package co.origon.api.repository.ofy;

import static com.googlecode.objectify.ObjectifyService.ofy;

import co.origon.api.model.EntityKey;
import co.origon.api.model.client.Device;
import co.origon.api.model.client.Member;
import co.origon.api.model.client.Membership;
import co.origon.api.model.client.Origo;
import co.origon.api.model.client.ReplicatedEntityRef;
import co.origon.api.model.client.ofy.ODevice;
import co.origon.api.model.client.ofy.OMember;
import co.origon.api.model.client.ofy.OMembership;
import co.origon.api.model.client.ofy.OOrigo;
import co.origon.api.model.client.ofy.OReplicatedEntity;
import co.origon.api.model.client.ofy.OReplicatedEntityRef;
import co.origon.api.repository.Repository;
import com.googlecode.objectify.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;

@Singleton
public class OfyRepository<T, U extends OfyMapper<T>> implements Repository<T> {

  private Class<U> ofyClass;

  public OfyRepository(Class<U> ofyClass) {
    this.ofyClass = ofyClass;
  }

  @Override
  public Optional<T> getById(String id) {
    return Optional.ofNullable(ofy().load().key(ofyKeyFromId(id)).now()).map(OfyMapper::fromOfy);
  }

  @Override
  public Optional<T> getByKey(EntityKey key) {
    return Optional.ofNullable(ofy().load().key(ofyKeyFromKey(key)).now()).map(OfyMapper::fromOfy);
  }

  @Override
  public Collection<T> getByIds(Collection<String> ids) {
    return ofy().load().keys(ofyKeysFromIds(ids)).values().stream()
        .map(OfyMapper::fromOfy)
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<T> getByKeys(Collection<EntityKey> keys) {
    return ofy().load().keys(keys.stream().map(this::ofyKeyFromKey).collect(Collectors.toSet()))
        .values().stream()
        .map(OfyMapper::fromOfy)
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<T> getByParentId(String parentId) {
    return getByParentId(parentId, null);
  }

  @Override
  public Collection<T> getByParentId(String parentId, Date modifiedAfter) {
    return ofy().load().type(ofyClass).ancestor(Key.create(OOrigo.class, parentId))
        .filter("dateReplicated >", modifiedAfter != null ? modifiedAfter : new Date(0L)).list()
        .stream()
        .map(OfyMapper::fromOfy)
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<T> getByPropertyValue(String property, Object value) {
    return ofy().load().type(ofyClass).filter(property, value).list().stream()
        .map(OfyMapper::fromOfy)
        .collect(Collectors.toSet());
  }

  @Override
  public T save(T entity) {
    ofy().save().entity(ofyEntityFromEntity(entity)).now();
    return entity;
  }

  @Override
  public void save(Collection<T> entities) {
    ofy().save().entities(ofyEntitiesFromEntities(entities));
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

  private Key<U> ofyKeyFromId(String id) {
    return Key.create(ofyClass, id);
  }

  private Key<U> ofyKeyFromKey(EntityKey entityKey) {
    return entityKey.parentId() != null
        ? Key.create(Key.create(OOrigo.class, entityKey.parentId()), ofyClass, entityKey.entityId())
        : ofyKeyFromId(entityKey.entityId());
  }

  private Collection<Key<U>> ofyKeysFromIds(Collection<String> ids) {
    return ids.stream().map(this::ofyKeyFromId).collect(Collectors.toSet());
  }

  private Collection<Key<U>> ofyKeysFromKeys(Collection<EntityKey> keys) {
    return keys.stream().map(this::ofyKeyFromKey).collect(Collectors.toSet());
  }

  private U ofyEntityFromEntity(T entity) {
    try {
      return ofyClass.equals(OReplicatedEntity.class)
          ? ofyEntityFromReplicatedEntity(entity)
          : ofyClass.getDeclaredConstructor(entity.getClass()).newInstance(entity);
    } catch (Exception e) {
      throw new InternalServerErrorException("Error constructing Ofy class from entity", e);
    }
  }

  private Collection<U> ofyEntitiesFromEntities(Collection<T> entities) {
    return entities.stream().map(this::ofyEntityFromEntity).collect(Collectors.toSet());
  }

  @SuppressWarnings("unchecked")
  private U ofyEntityFromReplicatedEntity(T entity) {
    if (entity.getClass().equals(Device.class)) {
      return (U) new ODevice((Device) entity);
    }
    if (entity.getClass().equals(Member.class)) {
      return (U) new OMember((Member) entity);
    }
    if (entity.getClass().equals(Membership.class)) {
      return (U) new OMembership((Membership) entity);
    }
    if (entity.getClass().equals(Origo.class)) {
      return (U) new OOrigo((Origo) entity);
    }
    if (entity.getClass().equals(ReplicatedEntityRef.class)) {
      return (U) new OReplicatedEntityRef((ReplicatedEntityRef) entity);
    }
    throw new InternalServerErrorException("Entity class is not mapped to an Ofy class");
  }
}
