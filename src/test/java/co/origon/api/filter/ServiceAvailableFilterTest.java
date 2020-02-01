package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.origon.api.common.Config;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceAvailableFilterTest {

  @Mock private ContainerRequestContext requestContext;
  @Mock private com.typesafe.config.Config systemConfig;

  private ServiceAvailableFilter serviceAvailableFilter;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      serviceAvailableFilter = new ServiceAvailableFilter(systemConfig);
    }

    @Test
    @DisplayName("Given system status is OK, then run to completion")
    void givenSystemStatusIsOk_thenRunToCoompletion() {
      // given
      when(systemConfig.getString(Config.SYSTEM_STATUS)).thenReturn(Config.SYSTEM_STATUS_OK);

      // when
      serviceAvailableFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given available but nok OK system status, then throw ServiceUnavailableException")
    void givenAvailableButNotOkSystemStatus_thenThrowServiceUnavailableException() {
      // given
      when(systemConfig.getString(Config.SYSTEM_STATUS)).thenReturn(null, Config.SYSTEM_STATUS_DOWN);

      assertAll(
          "System status not OK or null",
          // then
          () -> {
            final Throwable t =
                assertThrows(
                    ServiceUnavailableException.class,
                    () ->
                        // when
                        serviceAvailableFilter.filter(requestContext));
            assertEquals("Service unavailable. System status: null", t.getMessage());
          },

          // then
          () -> {
            final Throwable u =
                assertThrows(
                    ServiceUnavailableException.class,
                    () ->
                        // when
                        serviceAvailableFilter.filter(requestContext));
            assertEquals(
                "Service unavailable. System status: " + Config.SYSTEM_STATUS_DOWN, u.getMessage());
          });
    }

    @Test
    @DisplayName(
        "Given exception thrown when querying system status, then throw ServiceUnavailableException")
    void givenExceptionThrownWhenQueryingSystemStatus_thenThrowServiceUnavailableException() {
      // given
      when(systemConfig.getString(Config.SYSTEM_STATUS))
          .thenThrow(new RuntimeException("Yup, we're testing"));

      // then
      Throwable e =
          assertThrows(
              ServiceUnavailableException.class,
              () ->
                  // when
                  serviceAvailableFilter.filter(requestContext));
      assertEquals("System status unavailable: Yup, we're testing", e.getMessage());
    }
  }
}
