package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import co.origon.api.common.Session;
import co.origon.api.common.UrlParams;
import co.origon.api.model.server.DeviceCredentials;
import co.origon.api.repository.Repository;
import java.util.Date;
import java.util.Optional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
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
class ValidSessionDataFilterTest {

  private static final String VALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";
  private static final String VALID_DEVICE_ID = "e53f352b-84c6-4b8a-8065-05b53a54c7a1";

  @Mock private Repository<DeviceCredentials> deviceCredentialsRepository;
  @Mock private ContainerRequestContext requestContext;
  @Mock private UriInfo uriInfo;
  @Mock private MultivaluedMap<String, String> queryParameters;

  private ValidSessionDataFilter validSessionDataFilter;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      validSessionDataFilter = new ValidSessionDataFilter(deviceCredentialsRepository);
    }

    @Test
    @DisplayName("Given valid session data in requestContext, then run to completion")
    void givenValidSessionDataInRequestContext_thenRunToCompletion() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      lenient()
          .when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN))
          .thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(queryParameters.getFirst(UrlParams.APP_VERSION)).thenReturn("1.0");
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_ID)).thenReturn(VALID_DEVICE_ID);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TYPE)).thenReturn("Device");

      // when
      validSessionDataFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given valid session data in requestContext, then run to completion")
    void givenValidSessionDataInDeviceCredentials_thenRunToCompletion() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      lenient()
          .when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN))
          .thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(queryParameters.getFirst(UrlParams.APP_VERSION)).thenReturn("1.0");
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_ID)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TYPE)).thenReturn(null);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN))
          .thenReturn(
              Optional.of(
                  DeviceCredentials.builder()
                      .email("some")
                      .deviceId(VALID_DEVICE_ID)
                      .deviceType("some")
                      .authToken(VALID_DEVICE_TOKEN)
                      .dateExpires(new Date(System.currentTimeMillis() + 1000L))
                      .build()));

      // when
      validSessionDataFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given no session data in requestContext, then throw BadRequestException")
    void givenNoSessionDataInRequestContext_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.APP_VERSION)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_ID)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TYPE)).thenReturn(null);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validSessionDataFilter.filter(requestContext));
      assertEquals("Invalid session data", e.getMessage());
    }

    @Test
    @DisplayName("Given incomplete session data in requestContext, then throw BadRequestException")
    void givenIncompleteSessionDataInRequestContext_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.APP_VERSION)).thenReturn("1.0");
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_ID)).thenReturn(VALID_DEVICE_ID);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TYPE)).thenReturn(null);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validSessionDataFilter.filter(requestContext));
      assertEquals("Invalid session data", e.getMessage());
    }

    @Test
    @DisplayName(
        "Given incompletee session data and unknown device token, then throw BadRequestException")
    void givenIncompleteSessionDataAndUnknownDeviceToken_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      lenient()
          .when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN))
          .thenReturn(VALID_DEVICE_TOKEN);
      lenient().when(queryParameters.getFirst(UrlParams.APP_VERSION)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_ID)).thenReturn(null);
      lenient().when(queryParameters.getFirst(UrlParams.DEVICE_TYPE)).thenReturn(null);
      when(deviceCredentialsRepository.getById(VALID_DEVICE_TOKEN)).thenReturn(Optional.empty());

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validSessionDataFilter.filter(requestContext));
      assertEquals("Incomplete session data and unknown device token", e.getMessage());
    }

    @AfterEach
    void tearDown() {
      Session.dispose();
    }
  }
}
