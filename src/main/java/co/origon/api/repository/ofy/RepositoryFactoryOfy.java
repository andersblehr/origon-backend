package co.origon.api.repository.ofy;

import co.origon.api.model.DeviceCredentials;
import co.origon.api.model.MemberProxy;
import co.origon.api.model.OneTimeCredentials;
import co.origon.api.model.api.entity.Membership;
import co.origon.api.model.api.entity.Origo;
import co.origon.api.model.api.entity.ReplicatedEntity;
import co.origon.api.model.ofy.entity.OAuthInfo;
import co.origon.api.model.ofy.entity.OAuthMeta;
import co.origon.api.model.ofy.entity.OMemberProxy;
import co.origon.api.model.ofy.entity.OMembership;
import co.origon.api.model.ofy.entity.OOrigo;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.api.repository.api.Repository;
import co.origon.api.repository.api.RepositoryFactory;
import javax.inject.Singleton;

@Singleton
public class RepositoryFactoryOfy implements RepositoryFactory {

  @Override
  @SuppressWarnings("unchecked")
  public <E> Repository<E> repositoryFor(Class<E> clazz) {
    if (clazz.equals(DeviceCredentials.class)) {
      return (Repository<E>) new RepositoryOfy<>(OAuthMeta.class);
    }
    if (clazz.equals(MemberProxy.class)) {
      return (Repository<E>) new RepositoryOfy<>(OMemberProxy.class);
    }
    if (clazz.equals(OneTimeCredentials.class)) {
      return (Repository<E>) new RepositoryOfy<>(OAuthInfo.class);
    }
    if (clazz.equals(Membership.class)) {
      return (Repository<E>) new RepositoryOfy<>(OMembership.class);
    }
    if (clazz.equals(Origo.class)) {
      return (Repository<E>) new RepositoryOfy<>(OOrigo.class);
    }
    if (clazz.equals(ReplicatedEntity.class)) {
      return (Repository<E>) new RepositoryOfy<>(OReplicatedEntity.class);
    }
    return null;
  }
}
