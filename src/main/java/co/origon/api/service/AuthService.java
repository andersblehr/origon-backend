package co.origon.api.service;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.Mailer;
import co.origon.api.common.Mailer.Language;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.model.server.OneTimeCredentials;
import co.origon.api.repository.Repository;
import co.origon.api.repository.RepositoryFactory;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

@Singleton
public class AuthService {

  public static final String WWW_AUTH_CHALLENGE_BASIC_AUTH = "login";

  private static final Logger LOG = Logger.getLogger(AuthService.class.getName());
  private static final int ACTIVATION_CODE_LENGTH = 6;

  @Context private ContainerRequestContext requestContext;

  private final Repository<MemberProxy> memberProxyRepository;
  private final Repository<OneTimeCredentials> oneTimeCredentialsRepository;
  private final Repository<DeviceCredentials> deviceCredentialsRepository;
  private final Mailer mailer;

  private final Supplier<RuntimeException> userDoesNotExistThrower =
      () -> new NotAuthorizedException("User does not exist");

  @Inject
  public AuthService(RepositoryFactory repositoryFactory, Mailer mailer) {
    this.memberProxyRepository = repositoryFactory.repositoryFor(MemberProxy.class);
    this.oneTimeCredentialsRepository = repositoryFactory.repositoryFor(OneTimeCredentials.class);
    this.deviceCredentialsRepository = repositoryFactory.repositoryFor(DeviceCredentials.class);
    this.mailer = mailer;
  }

  public AuthService(
      Repository<MemberProxy> memberProxyRepository,
      Repository<OneTimeCredentials> oneTimeCredentialsRepository,
      Repository<DeviceCredentials> deviceCredentialsRepository,
      Mailer mailer) {

    this.memberProxyRepository = memberProxyRepository;
    this.oneTimeCredentialsRepository = oneTimeCredentialsRepository;
    this.deviceCredentialsRepository = deviceCredentialsRepository;
    this.mailer = mailer;
  }

  public Optional<DeviceCredentials> getDeviceCredentials(String deviceToken) {
    return deviceCredentialsRepository.getById(deviceToken);
  }

  public OneTimeCredentials registerUser(
      BasicAuthCredentials basicAuthCredentials, String deviceId, Language language) {

    checkUserStatus(basicAuthCredentials.email(), false);
    final String activationCode = UUID.randomUUID().toString().substring(0, ACTIVATION_CODE_LENGTH);
    mailer.using(language).sendRegistrationEmail(basicAuthCredentials.email(), activationCode);
    LOG.fine("Sent user activation code to new user " + basicAuthCredentials.email());

    return oneTimeCredentialsRepository.save(
        OneTimeCredentials.builder()
            .deviceId(deviceId)
            .email(basicAuthCredentials.email())
            .passwordHash(basicAuthCredentials.passwordHash())
            .activationCode(activationCode)
            .build());
  }

  public MemberProxy activateUser(DeviceCredentials deviceCredentials, String passwordHash) {
    final MemberProxy userProxy =
        checkUserStatus(deviceCredentials.email(), false)
            .orElse(
                MemberProxy.builder()
                    .id(deviceCredentials.email())
                    .passwordHash(passwordHash)
                    .build());
    final OneTimeCredentials oneTimeCredentials =
        oneTimeCredentialsRepository
            .getById(deviceCredentials.email())
            .orElseThrow(() -> new BadRequestException("User is not awaiting activation"));
    if (!oneTimeCredentials.passwordHash().equals(passwordHash)) {
      throw new NotAuthorizedException("Incorrect password", WWW_AUTH_CHALLENGE_BASIC_AUTH);
    }
    deviceCredentialsRepository.save(deviceCredentials);
    oneTimeCredentialsRepository.deleteById(deviceCredentials.email());
    LOG.fine("Persisted new device token for user " + deviceCredentials.email());

    return memberProxyRepository.save(userProxy);
  }

  public MemberProxy loginUser(DeviceCredentials deviceCredentials, String passwordHash) {
    final MemberProxy userProxy =
        checkUserStatus(deviceCredentials.email(), true).orElseThrow(userDoesNotExistThrower);
    if (!userProxy.passwordHash().equals(passwordHash)) {
      throw new NotAuthorizedException("Invalid password");
    }
    deviceCredentialsRepository.save(deviceCredentials);
    LOG.fine("Persisted new device token for user " + deviceCredentials.email());

    return memberProxyRepository.save(
        reauthoriseUserDevice(
            userProxy, deviceCredentials.deviceId(), deviceCredentials.authToken()));
  }

  public void changePassword(BasicAuthCredentials credentials) {
    memberProxyRepository.save(
        memberProxyRepository
            .getById(credentials.email())
            .orElseThrow(userDoesNotExistThrower)
            .withPasswordHash(credentials.passwordHash()));
    LOG.fine("Saved new password hash for " + credentials.email());
  }

  public void resetPassword(BasicAuthCredentials credentials, Language language) {
    memberProxyRepository.save(
        memberProxyRepository
            .getById(credentials.email())
            .orElseThrow(userDoesNotExistThrower)
            .withPasswordHash(credentials.passwordHash()));
    mailer.using(language).sendPasswordResetEmail(credentials.email(), credentials.password());
    LOG.fine("Sent temporary password to " + credentials.email());
  }

  public OneTimeCredentials sendActivationCode(
      BasicAuthCredentials basicAuthCredentials, String deviceToken, Language language) {

    final DeviceCredentials deviceCredentials =
        deviceCredentialsRepository
            .getById(deviceToken)
            .orElseThrow(() -> new NotAuthorizedException("Unknown device token"));
    final OneTimeCredentials oneTimeCredentials =
        oneTimeCredentialsRepository.save(
            OneTimeCredentials.builder()
                .deviceId(deviceCredentials.deviceId())
                .email(basicAuthCredentials.email())
                .activationCode(basicAuthCredentials.password()) // Not pretty...
                .build());
    mailer
        .using(language)
        .sendEmailActivationCode(oneTimeCredentials.email(), oneTimeCredentials.activationCode());
    LOG.fine("Sent email activation code to " + oneTimeCredentials.email());

    return oneTimeCredentials;
  }

  private MemberProxy reauthoriseUserDevice(
      MemberProxy userProxy, String deviceId, String deviceToken) {

    final Collection<String> revokedDeviceTokens =
        deviceCredentialsRepository.getByIds(userProxy.deviceTokens()).stream()
            .filter(deviceCredentials -> deviceCredentials.deviceId().equals(deviceId))
            .map(DeviceCredentials::authToken)
            .collect(Collectors.toSet());
    deviceCredentialsRepository.deleteByIds(revokedDeviceTokens);

    return userProxy.withDeviceTokens(
        Stream.concat(
                Stream.of(deviceToken),
                userProxy.deviceTokens().stream()
                    .filter(token -> !revokedDeviceTokens.contains(token)))
            .collect(Collectors.toSet()));
  }

  private Optional<MemberProxy> checkUserStatus(String userEmail, boolean isRegistered) {
    final Optional<MemberProxy> userProxy = memberProxyRepository.getById(userEmail);
    if (!isRegistered && userProxy.isPresent() && userProxy.get().isRegistered()) {
      throw new WebApplicationException("User is already registered and active", Status.CONFLICT);
    }
    if (isRegistered && userProxy.isPresent() && !userProxy.get().isRegistered()) {
      throw new BadRequestException("User is not registered, cannot authenticate");
    }
    return userProxy;
  }
}
