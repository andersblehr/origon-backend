package co.origon.api.filter;

import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@ValidSessionData
@Priority(4)
public class ValidSessionDataFilter implements ContainerRequestFilter {

  private final Repository<DeviceCredentials> deviceCredentialsRepository;

  @Inject
  public ValidSessionDataFilter(RepositoryFactory repositoryFactory) {
    deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
  }

  ValidSessionDataFilter(Repository<DeviceCredentials> deviceCredentialsRepository) {
    this.deviceCredentialsRepository = deviceCredentialsRepository;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String deviceToken =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TOKEN);
    final String appVersion =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.APP_VERSION);
    String deviceId =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_ID);
    String deviceType =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TYPE);

    if (isSet(deviceToken) && !isSet(deviceId) && !isSet(deviceType)) {
      final DeviceCredentials deviceCredentials =
          deviceCredentialsRepository
              .getById(deviceToken)
              .orElseThrow(
                  () ->
                      new BadRequestException("Incomplete session data and unknown device token"));
      deviceId = deviceCredentials.deviceId();
      deviceType = deviceCredentials.deviceType();
    }

    try {
      Session.create(deviceId, deviceType, appVersion);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid session data", e);
    }
  }

  private boolean isSet(String string) {
    return string != null && string.length() > 0;
  }
}
