package co.origon.api.repository.ofy;

import co.origon.api.model.ReplicatedEntity;
import co.origon.api.model.client.Membership;
import co.origon.api.model.client.Origo;
import co.origon.api.model.ofy.OAuthInfo;
import co.origon.api.model.ofy.OAuthMeta;
import co.origon.api.model.ofy.OMemberProxy;
import co.origon.api.model.ofy.OMembership;
import co.origon.api.model.ofy.OOrigo;
import co.origon.api.model.ofy.OReplicatedEntity;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.model.server.OneTimeCredentials;
import co.origon.api.repository.api.Repository;
import co.origon.api.repository.api.RepositoryFactory;
import javax.inject.Singleton;

@Singleton
public class RepositoryFactoryOfy implements RepositoryFactory {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Repository<T> repositoryFor(Class<T> clazz) {
    if (clazz.equals(DeviceCredentials.class)) {
      return (Repository<T>) new RepositoryOfy<>(OAuthMeta.class);
    }
    if (clazz.equals(MemberProxy.class)) {
      return (Repository<T>) new RepositoryOfy<>(OMemberProxy.class);
    }
    if (clazz.equals(OneTimeCredentials.class)) {
      return (Repository<T>) new RepositoryOfy<>(OAuthInfo.class);
    }
    if (clazz.equals(Membership.class)) {
      return (Repository<T>) new RepositoryOfy<>(OMembership.class);
    }
    if (clazz.equals(Origo.class)) {
      return (Repository<T>) new RepositoryOfy<>(OOrigo.class);
    }
    if (clazz.equals(ReplicatedEntity.class)) {
      return (Repository<T>) new RepositoryOfy<>(OReplicatedEntity.class);
    }
    throw new IllegalArgumentException("No Ofy mapping for class: " + clazz.getSimpleName());
  }
}
