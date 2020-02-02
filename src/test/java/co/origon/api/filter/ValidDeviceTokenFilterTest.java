package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.origon.api.common.BasicAuthCredentials;
import co.origon.api.common.UrlParams;
import co.origon.api.controller.AuthController;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.model.server.MemberProxy;
import co.origon.api.repository.Repository;
import java.util.Date;
import java.util.Optional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidDeviceTokenFilterTest {

  private static final String USER_ID = "3eca0277-b8aa-4293-a028-44e0746c3082";
  private static final String USER_EMAIL = "user@example.com";
  private static final String BASIC_AUTH_CREDENTIALS = "Basic " + encode(USER_EMAIL + ":password");
  private static final String NON_MATCHING_CREDENTIALS =
      "Basic " + encode("other@email.com:password");
  private static final String DEVICE_ID = "e53f352b-84c6-4b8a-8065-05b53a54c7a1";
  private static final String VALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";
  private static final String INVALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61XYZ";

  @Mock private Repository<DeviceCredentials> deviceCredentialsRepository;
  @Mock private Repository<MemberProxy> userProxyRepository;
  @Mock private ContainerRequestContext requestContext;
  @Mock private UriInfo uriInfo;
  @Mock private MultivaluedMap<String, String> queryParameters;

  private ValidDeviceTokenFilter validDeviceTokenFilter;

  private final MemberProxy registeredUserProxy =
      MemberProxy.builder()
          .id(USER_EMAIL)
          .memberId(USER_ID)
          .passwordHash("some")
          .deviceToken(VALID_DEVICE_TOKEN)
          .build();
  private final MemberProxy unregisteredUserProxy =
      MemberProxy.builder()
          .id(USER_EMAIL)
          .memberId(USER_ID)
          .deviceToken(VALID_DEVICE_TOKEN)
          .build();
  private final DeviceCredentials validDeviceCredentials =
      DeviceCredentials.builder()
          .userEmail(USER_EMAIL)
          .deviceId(DEVICE_ID)
          .deviceToken(VALID_DEVICE_TOKEN)
          .deviceType("some")
          .expiresAt(new Date(System.currentTimeMillis() + 1000L))
          .build();
  private final DeviceCredentials expiredDeviceCredentials =
      DeviceCredentials.builder()
          .userEmail(USER_EMAIL)
          .deviceId(DEVICE_ID)
          .deviceToken(VALID_DEVICE_TOKEN)
          .deviceType("some")
          .expiresAt(new Date(System.currentTimeMillis() - 1000L))
          .build();

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      BasicAuthCredentials.validate(BASIC_AUTH_CREDENTIALS);
      validDeviceTokenFilter =
          new ValidDeviceTokenFilter(deviceCredentialsRepository, userProxyRepository);
    }

    @Test
    @DisplayName("Given valid device token, run to completion")
    void givenValidDeviceToken_thenRunToCompletion() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(Optional.of(validDeviceCredentials));
      when(userProxyRepository.getById(USER_EMAIL)).thenReturn(Optional.of(registeredUserProxy));

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
      assertEquals("Invalid device token", e.getMessage());
    }

    @Test
    @DisplayName("Given unknown device token, throw BadRequestException")
    void givenUnknownDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN)).thenReturn(Optional.empty());

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
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(Optional.of(expiredDeviceCredentials));

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
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(Optional.of(validDeviceCredentials));
      when(userProxyRepository.getById(USER_EMAIL)).thenReturn(Optional.empty());

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Cannot authenticate unknown user", e.getMessage());
    }

    @Test
    @DisplayName("Given unknown user, throw BadRequestException")
    void givenNonRegisteredUser_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(Optional.of(validDeviceCredentials));
      when(userProxyRepository.getById(USER_EMAIL)).thenReturn(Optional.of(unregisteredUserProxy));

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validDeviceTokenFilter.filter(requestContext));
      assertEquals("Cannot authenticate inactive user", e.getMessage());
    }

    @Test
    @DisplayName("Given non-matching credentials, throw BadRequestException")
    void givenNonMatchingCredentials_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(VALID_DEVICE_TOKEN);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(Optional.of(validDeviceCredentials));
      when(userProxyRepository.getById(USER_EMAIL)).thenReturn(Optional.of(registeredUserProxy));

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

  public static String encode(String toBeEncoded) {
    return new String(java.util.Base64.getEncoder().encode(toBeEncoded.getBytes()));
  }
}
