package co.origon.api.repository.ofy;

import co.origon.api.model.client.Membership;
import co.origon.api.model.client.Origo;
import co.origon.api.model.client.ReplicatedEntity;
import co.origon.api.model.client.ofy.OMembership;
import co.origon.api.model.client.ofy.OOrigo;
import co.origon.api.model.client.ofy.OReplicatedEntity;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.model.server.OneTimeCredentials;
import co.origon.api.model.server.ofy.OAuthInfo;
import co.origon.api.model.server.ofy.OAuthMeta;
import co.origon.api.model.server.ofy.OMemberProxy;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import javax.inject.Singleton;

@Singleton
public class OfyRepositoryFactory implements RepositoryFactory {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Repository<T> repositoryFor(Class<T> clazz) {
    if (clazz.equals(DeviceCredentials.class)) {
      return (Repository<T>) new OfyRepository<>(OAuthMeta.class);
    }
    if (clazz.equals(MemberProxy.class)) {
      return (Repository<T>) new OfyRepository<>(OMemberProxy.class);
    }
    if (clazz.equals(OneTimeCredentials.class)) {
      return (Repository<T>) new OfyRepository<>(OAuthInfo.class);
    }
    if (clazz.equals(Membership.class)) {
      return (Repository<T>) new OfyRepository<>(OMembership.class);
    }
    if (clazz.equals(Origo.class)) {
      return (Repository<T>) new OfyRepository<>(OOrigo.class);
    }
    if (clazz.equals(ReplicatedEntity.class)) {
      return (Repository<T>) new OfyRepository<>(OReplicatedEntity.class);
    }
    throw new IllegalArgumentException("No Ofy mapping for class: " + clazz.getSimpleName());
  }
}
