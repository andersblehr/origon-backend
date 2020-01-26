package co.origon.api.filter;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.UrlParams;
import co.origon.api.controller.AuthController;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

import static co.origon.api.common.Base64.encode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidDeviceTokenFilterTest {

  private static final String VALID_EMAIL = "user@example.com";
  private static final String VALID_CREDENTIALS = "Basic " + encode(VALID_EMAIL + ":password");
  private static final String NON_MATCHING_CREDENTIALS =
      "Basic " + encode("other@email.com:password");
  private static final String VALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";
  private static final String INVALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61XYZ";

  @Mock private DaoFactory daoFactory;
  @Mock private Dao<DeviceCredentials> deviceCredentialsDao;
  @Mock private DeviceCredentials deviceCredentials;
  @Mock private Dao<MemberProxy> userProxyDao;
  @Mock private MemberProxy userProxy;
  @Mock private ContainerRequestContext requestContext;
  @Mock private UriInfo uriInfo;
  @Mock private MultivaluedMap<String, String> queryParameters;

  private ValidDeviceTokenFilter validDeviceTokenFilter;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      BasicAuthCredentials.validate(VALID_CREDENTIALS);
      validDeviceTokenFilter = new ValidDeviceTokenFilter(daoFactory);
    }

    @Test
    @DisplayName("Given valid device token, run to completion")
    void givenValidDeviceToken_thenRunToCompletion() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.dateExpires())
          .thenReturn(new Date(System.currentTimeMillis() + 60000L));
      when(deviceCredentials.email()).thenReturn(VALID_EMAIL);
      when(userProxyDao.get(VALID_EMAIL)).thenReturn(userProxy);
      lenient().when(daoFactory.daoFor(MemberProxy.class)).thenReturn(userProxyDao);
      when(userProxy.isRegistered()).thenReturn(true);

      // when
      validDeviceTokenFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given no device token, throw BadRequestException")
    void givenNoDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(null, "");

      assertAll(
          "Missing device token",
          // then
          () -> {
            final Throwable eNull =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        validDeviceTokenFilter.filter(requestContext));
            assertEquals("Missing query parameter: " + UrlParams.DEVICE_TOKEN, eNull.getMessage());
          },

          // then
          () -> {
            final Throwable eEmpty =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        validDeviceTokenFilter.filter(requestContext));
            assertEquals("Missing query parameter: " + UrlParams.DEVICE_TOKEN, eEmpty.getMessage());
          });
    }

    @Test
    @DisplayName("Given invalid device token, throw BadRequestException")
    void givenInvalidDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(INVALID_DEVICE_TOKEN);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Invalid device token: " + INVALID_DEVICE_TOKEN, e.getMessage());
    }

    @Test
    @DisplayName("Given unknown device token, throw BadRequestException")
    void givenUnknownDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(null);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Cannot authenticate unknown device token", e.getMessage());
    }

    @Test
    @DisplayName("Given unknown device token, throw BadRequestException")
    void givenExpiredDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.dateExpires())
          .thenReturn(new Date(System.currentTimeMillis() - 60000L));

      // then
      final WebApplicationException e =
          assertThrows(
              NotAuthorizedException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Device token has expired", e.getMessage());
      final String authChallenge =
          e.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE).toString();
      assertEquals(AuthController.WWW_AUTH_CHALLENGE_BASIC_AUTH, authChallenge);
    }

    @Test
    @DisplayName("Given unknown user, throw BadRequestException")
    void givenUnknownUser_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.dateExpires())
          .thenReturn(new Date(System.currentTimeMillis() + 60000L));
      when(deviceCredentials.email()).thenReturn(VALID_EMAIL);
      lenient().when(daoFactory.daoFor(MemberProxy.class)).thenReturn(userProxyDao);
      when(userProxyDao.get(VALID_EMAIL)).thenReturn(null);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Cannot authenticate unknown or inactive user: " + VALID_EMAIL, e.getMessage());
    }

    @Test
    @DisplayName("Given unknown user, throw BadRequestException")
    void givenNonRegisteredUser_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.dateExpires())
          .thenReturn(new Date(System.currentTimeMillis() + 60000L));
      when(deviceCredentials.email()).thenReturn(VALID_EMAIL);
      lenient().when(daoFactory.daoFor(MemberProxy.class)).thenReturn(userProxyDao);
      when(userProxyDao.get(VALID_EMAIL)).thenReturn(userProxy);
      when(userProxy.isRegistered()).thenReturn(false);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Cannot authenticate unknown or inactive user: " + VALID_EMAIL, e.getMessage());
    }

    @Test
    @DisplayName("Given non-matching credentials, throw BadRequestException")
    void givenNonMatchingCredentials_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(daoFactory.daoFor(DeviceCredentials.class)).thenReturn(deviceCredentialsDao);
      when(deviceCredentialsDao.get(VALID_DEVICE_TOKEN)).thenReturn(deviceCredentials);
      when(deviceCredentials.dateExpires())
          .thenReturn(new Date(System.currentTimeMillis() + 60000L));
      when(deviceCredentials.email()).thenReturn(VALID_EMAIL);
      lenient().when(daoFactory.daoFor(MemberProxy.class)).thenReturn(userProxyDao);
      when(userProxyDao.get(VALID_EMAIL)).thenReturn(userProxy);
      when(userProxy.isRegistered()).thenReturn(true);

      BasicAuthCredentials.dispose();
      BasicAuthCredentials.validate(NON_MATCHING_CREDENTIALS);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals(
          "Basic auth credentials do not match records for provided device token", e.getMessage());
    }

    @AfterEach
    void tearDown() {
      BasicAuthCredentials.dispose();
    }
  }
}
