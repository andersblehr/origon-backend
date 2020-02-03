package co.origon.api.filter;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.UrlParams;
import co.origon.api.controller.AuthController;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import java.util.Date;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@ValidDeviceToken
@Priority(3)
public class ValidDeviceTokenFilter implements ContainerRequestFilter {

  private final Repository<DeviceCredentials> deviceCredentialsRepository;
  private final Repository<MemberProxy> memberProxyRepository;

  @Inject
  public ValidDeviceTokenFilter(RepositoryFactory repositoryFactory) {
    deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
    memberProxyRepository = repositoryFactory.repositoryFor(MemberProxy.class);
  }

  ValidDeviceTokenFilter(
      Repository<DeviceCredentials> deviceCredentialsRepository,
      Repository<MemberProxy> memberProxyRepository) {
    this.deviceCredentialsRepository = deviceCredentialsRepository;
    this.memberProxyRepository = memberProxyRepository;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String deviceToken =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.DEVICE_TOKEN);
    if (deviceToken == null || deviceToken.length() == 0) {
      throw new BadRequestException("Missing query parameter: " + UrlParams.DEVICE_TOKEN);
    }
    if (deviceToken.length() != 40) {
      throw new BadRequestException("Invalid device token");
    }

    final DeviceCredentials deviceCredentials =
        deviceCredentialsRepository
            .getById(deviceToken)
            .orElseThrow(() -> new BadRequestException("Cannot authenticate unknown device token"));
    if (deviceCredentials.dateExpires().before(new Date())) {
      throw new NotAuthorizedException(
          "Device token has expired", AuthController.WWW_AUTH_CHALLENGE_BASIC_AUTH);
    }

    final MemberProxy userProxy =
        memberProxyRepository
            .getById(deviceCredentials.email())
            .orElseThrow(() -> new BadRequestException("Cannot authenticate unknown user"));
    if (!userProxy.isRegistered()) {
      throw new BadRequestException("Cannot authenticate inactive user");
    }

    if (BasicAuthCredentials.hasCredentials()
        && !BasicAuthCredentials.getCredentials().email().equals(deviceCredentials.email()))
      throw new BadRequestException(
          "Basic auth credentials do not match records for provided device token");
  }
}
