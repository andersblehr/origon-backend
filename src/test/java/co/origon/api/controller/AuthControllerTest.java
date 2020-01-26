package co.origon.api.controller;

import co.origon.api.common.*;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;

import co.origon.api.model.api.entity.OtpCredentials;
import co.origon.api.model.ofy.entity.OReplicatedEntity;
import co.origon.mailer.api.Mailer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;

import static co.origon.api.common.Base64.encode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private static final String USER_ID = "3eca0277-b8aa-4293-a028-44e0746c3082";
  private static final String USER_EMAIL = "user@example.com";
  private static final String USER_PASSWORD = "password";
  private static final String AUTHORIZATION_HEADER =
      "Basic " + encode(USER_EMAIL + ":" + USER_PASSWORD);
  private static final String DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";
  private static final String DEVICE_ID = "e53f352b-84c6-4b8a-8065-05b53a54c7a1";
  private static final String DEVICE_TYPE = "Device";
  private static final String APP_VERSION = "1.0";
  private static final String LANGUAGE = "nb";
  private static final String ACTIVATION_CODE = "abcdef";

  @Mock private DaoFactory daoFactory;
  @Mock private Dao<MemberProxy> userProxyDao;
  @Mock private MemberProxy userProxy;
  @Mock private Dao<OtpCredentials> otpCredentialsDao;
  @Mock private OtpCredentials otpCredentials;
  @Mock private Dao<DeviceCredentials> deviceCredentialsDao;
  @Mock private DeviceCredentials deviceCredentials;
  @Mock private ODao legacyDao;
  @Mock private List<OReplicatedEntity> fetchedEntities;
  @Mock private Mailer mailer;

  @InjectMocks private AuthController authController;

  @BeforeEach
  void setUp() {
    BasicAuthCredentials.validate(AUTHORIZATION_HEADER);
    Session.create(DEVICE_ID, DEVICE_TYPE, APP_VERSION);
  }

  @Nested
  @DisplayName("GET /auth/register")
  class WhenRegisterUser {

    @Test
    @DisplayName("Given new user, then return OTP credentials")
    void givenNewUser_thenReturnOtpCredentials() {
      // given
      establishDidRegister(false);

      // given: Create OTP credentials
      lenient().when(daoFactory.daoFor(OtpCredentials.class)).thenReturn(otpCredentialsDao);
      when(otpCredentialsDao.create()).thenReturn(otpCredentials);
      when(otpCredentials.deviceId(DEVICE_ID)).thenReturn(otpCredentials);
      when(otpCredentials.email(anyString())).thenReturn(otpCredentials);
      when(otpCredentials.passwordHash(anyString())).thenReturn((otpCredentials));
      lenient().when(otpCredentials.activationCode(anyString())).thenReturn(otpCredentials);

      // given: Send registration email
      when(mailer.language(anyString())).thenReturn(mailer);
      lenient().when(otpCredentials.activationCode()).thenReturn(ACTIVATION_CODE);

      // when
      final Response response =
          authController.registerUser(
              AUTHORIZATION_HEADER, DEVICE_ID, DEVICE_TYPE, APP_VERSION, LANGUAGE);

      // then
      verify(otpCredentials).deviceId(DEVICE_ID);
      verify(otpCredentials).email(USER_EMAIL);
      verify(otpCredentials).passwordHash(BasicAuthCredentials.getCredentials().passwordHash());
      verify(otpCredentials).activationCode(anyString());
      verify(otpCredentialsDao).save(otpCredentials);
      verify(mailer).sendRegistrationEmail(USER_EMAIL, ACTIVATION_CODE);

      assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
      assertEquals(otpCredentials, response.getEntity());
    }

    @Test
    @DisplayName("Given existing user, then return 409 CONFLICT")
    void givenExistingUser_thenReturn409Conflict() {
      // given
      establishDidRegister(true);

      // then
      WebApplicationException e =
          assertThrows(
              WebApplicationException.class,
              () ->
                  // when
                  authController.registerUser(
                      AUTHORIZATION_HEADER, DEVICE_ID, DEVICE_TYPE, APP_VERSION, LANGUAGE));
      assertEquals(Status.CONFLICT.getStatusCode(), e.getResponse().getStatus());
      assertEquals("User is already registered", e.getMessage());
    }
  }

  @Nested
  @DisplayName("GET /auth/activate")
  class WhenActivateUser {

    @Test
    @DisplayName("Given non-invited user, then activate user and return no user entities")
    void givenNonInvitedUser_thenActivateUserAndReturnNoUserEntities() {
      // given
      establishInvited(false);
      establishDidRegister(false);
      establishValidOtpCredentials(true);
      establishDeviceCredentialsCreated();
      establishActivated();

      // when
      Response response =
          authController.activateUser(
              AUTHORIZATION_HEADER, DEVICE_TOKEN, DEVICE_ID, DEVICE_TYPE, APP_VERSION);

      // then
      verifyDeviceCredentials();
      verifyUserProxy();
      verify(otpCredentialsDao).delete(otpCredentials);
      verify(legacyDao).fetchEntities(USER_EMAIL);

      assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
      assertNull(response.getLocation());
      assertNotNull(response.getLastModified());
    }

    @Test
    @DisplayName("Given invited user, then activate user and return user entities")
    void givenInvitedUser_thenActivateUserAndReturnUserEntities() {
      // given
      establishInvited(true);
      establishDidRegister(false);
      establishValidOtpCredentials(true);
      establishDeviceCredentialsCreated();
      establishActivated();

      // when
      Response response =
          authController.activateUser(
              AUTHORIZATION_HEADER, DEVICE_TOKEN, DEVICE_ID, DEVICE_TYPE, APP_VERSION);

      // then
      verifyDeviceCredentials();
      verifyUserProxy();
      verify(otpCredentialsDao).delete(otpCredentials);
      verify(legacyDao).fetchEntities(USER_EMAIL);

      assertEquals(Status.OK.getStatusCode(), response.getStatus());
      assertEquals(USER_ID, response.getLocation().toString());
      assertNotNull(response.getLastModified());
    }

    @Test
    @DisplayName("Given existing user, then return 400 BAD_REQUEST")
    void givenExistingUser_thenReturn400BadRequest() {
      // given
      establishDidRegister(true);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  authController.activateUser(
                      AUTHORIZATION_HEADER, DEVICE_TOKEN, DEVICE_ID, DEVICE_TYPE, APP_VERSION));
      assertEquals("Cannot activate an active user", e.getMessage());
    }

    @Test
    @DisplayName("Given no OTP credentials, then return 400 BAD_REQUEST")
    void givenNoOtpCredentials_thenReturn400BadRequest() {
      // given
      establishDidRegister(false);
      establishOtpCredentialsAvailable(false);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  authController.activateUser(
                      AUTHORIZATION_HEADER, DEVICE_TOKEN, DEVICE_ID, DEVICE_TYPE, APP_VERSION));
      assertEquals("User is not awaiting activation, cannot activate", e.getMessage());
    }

    @Test
    @DisplayName("Given mismatched credentials, then return 401 UNAUTHORIZED")
    void givenMismatchedCredentials_thenReturn401Unauthorized() {
      // given
      establishDidRegister(false);
      establishValidOtpCredentials(false);

      // then
      WebApplicationException e =
          assertThrows(
              NotAuthorizedException.class,
              () ->
                  // when
                  authController.activateUser(
                      AUTHORIZATION_HEADER, DEVICE_TOKEN, DEVICE_ID, DEVICE_TYPE, APP_VERSION));
      assertEquals("Incorrect password", e.getMessage());
      final String authChallenge =
          e.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE).toString();
      assertEquals(AuthController.WWW_AUTH_CHALLENGE_BASIC_AUTH, authChallenge);
    }

    @Test
    @DisplayName("Given invalid device token, then return 400 BAD_REQUEST")
    void givenInvalidDeviceToken_thenReturn400BadRequest() {
      // given
      establishDidRegister(false);
      establishValidOtpCredentials(true);

      // then
      assertAll(
          "Invalid device token",
          () -> {
            final Throwable eNull =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        authController.activateUser(
                            AUTHORIZATION_HEADER, null, DEVICE_ID, DEVICE_TYPE, APP_VERSION));
            assertEquals("Missing parameter: " + UrlParams.DEVICE_TOKEN, eNull.getMessage());
          },
          () -> {
            final Throwable eInvalid =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        authController.activateUser(
                            AUTHORIZATION_HEADER,
                            "Not a token",
                            DEVICE_ID,
                            DEVICE_TYPE,
                            APP_VERSION));
            assertEquals("Invalid device token format: Not a token", eInvalid.getMessage());
          });
    }

    private void establishInvited(boolean invited) {
      when(userProxy.memberId()).thenReturn(invited ? USER_ID : null);
      when(daoFactory.legacyDao()).thenReturn(legacyDao);
      when(legacyDao.fetchEntities(USER_EMAIL)).thenReturn(fetchedEntities);
      when(fetchedEntities.size()).thenReturn(invited ? 10 : 0);
    }

    private void establishOtpCredentialsAvailable(boolean available) {
      lenient().when(daoFactory.daoFor(OtpCredentials.class)).thenReturn(otpCredentialsDao);
      when(otpCredentialsDao.get(USER_EMAIL)).thenReturn(available ? otpCredentials : null);
    }

    private void establishValidOtpCredentials(boolean valid) {
      establishOtpCredentialsAvailable(true);
      when(otpCredentials.passwordHash())
          .thenReturn(valid ? BasicAuthCredentials.getCredentials().passwordHash() : "invalid");
    }

    private void establishDeviceCredentialsCreated() {
      lenient().when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.create()).thenReturn(deviceCredentials);
      when(deviceCredentials.deviceToken(DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.email(USER_EMAIL)).thenReturn(deviceCredentials);
      when(deviceCredentials.deviceId(DEVICE_ID)).thenReturn(deviceCredentials);
      when(deviceCredentials.deviceType(DEVICE_TYPE)).thenReturn(deviceCredentials);
    }

    private void establishActivated() {
      when(userProxyDao.produce(USER_EMAIL)).thenReturn(userProxy);
      when(userProxy.passwordHash(BasicAuthCredentials.getCredentials().passwordHash()))
          .thenReturn(userProxy);
      when(userProxy.deviceToken(DEVICE_TOKEN)).thenReturn(userProxy);
    }

    private void verifyDeviceCredentials() {
      verify(deviceCredentials).deviceToken(DEVICE_TOKEN);
      verify(deviceCredentials).email(USER_EMAIL);
      verify(deviceCredentials).deviceId(DEVICE_ID);
      verify(deviceCredentials).deviceType(DEVICE_TYPE);
      verify(deviceCredentialsDao).save(deviceCredentials);
    }

    private void verifyUserProxy() {
      verify(userProxy).passwordHash(BasicAuthCredentials.getCredentials().passwordHash());
      verify(userProxy).deviceToken(DEVICE_TOKEN);
      verify(userProxyDao).save(userProxy);
    }
  }

  private void establishDidRegister(boolean didRegister) {
    lenient().when(daoFactory.daoFor(MemberProxy.class)).thenReturn(userProxyDao);
    when(userProxyDao.get(USER_EMAIL)).thenReturn(userProxy);
    when(userProxy.isRegistered()).thenReturn(didRegister);
  }

  @AfterEach
  void tearDown() {
    BasicAuthCredentials.dispose();
    Session.dispose();
  }
}
