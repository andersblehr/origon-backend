package co.origon.api.service;

import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;
import co.origon.api.repository.api.Repository;
import co.origon.api.repository.api.RepositoryFactory;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthService {

  private final Repository<DeviceCredentials> deviceCredentialsRepository;

  @Inject
  public AuthService(RepositoryFactory repositoryFactory) {
    deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
  }

  public void refreshDeviceToken(MemberProxy userProxy, String deviceId, String deviceToken) {
    final Collection<String> redundantDeviceTokens =
        deviceCredentialsRepository.getByIds(userProxy.deviceTokens()).stream()
            .filter(deviceCredentials -> deviceCredentials.deviceId().equals(deviceId))
            .map(DeviceCredentials::deviceToken)
            .collect(Collectors.toSet());
    userProxy.deviceToken(deviceToken);
    userProxy.deviceTokens(
        userProxy.deviceTokens().stream()
            .filter(token -> !redundantDeviceTokens.contains(token))
            .collect(Collectors.toSet()));

    deviceCredentialsRepository.deleteByIds(redundantDeviceTokens);
  }
}
