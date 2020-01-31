package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.Config.Category;
import co.origon.api.model.api.entity.Config.Setting;
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

  private static final String STATUS_OK = "OK";
  private static final String STATUS_NOK = "testing";

  @Mock private DaoFactory daoFactory;
  @Mock private Dao<Config> configDao;
  @Mock private Config systemConfig;
  @Mock private ContainerRequestContext requestContext;

  private ServiceAvailableFilter serviceAvailableFilter;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      serviceAvailableFilter = new ServiceAvailableFilter(daoFactory);
    }

    @Test
    @DisplayName("Given system status is OK, then run to completion")
    void givenSystemStatusIsOk_thenRunToCoompletion() {
      // given
      when(daoFactory.daoFor(Config.class)).thenReturn(configDao);
      when(configDao.get(Category.SYSTEM)).thenReturn(systemConfig);
      when(systemConfig.getString(Setting.STATUS)).thenReturn(STATUS_OK);

      // when
      serviceAvailableFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given available but nok OK system status, then throw ServiceUnavailableException")
    void givenAvailableButNotOkSystemStatus_thenThrowServiceUnavailableException() {
      // given
      when(daoFactory.daoFor(Config.class)).thenReturn(configDao);
      when(configDao.get(Category.SYSTEM)).thenReturn(systemConfig);
      when(systemConfig.getString(Setting.STATUS)).thenReturn(null, STATUS_NOK);

      assertAll(
          "System status not OK or null",
          // then
          () -> {
            final Throwable eNull =
                assertThrows(
                    ServiceUnavailableException.class,
                    () ->
                        // when
                        serviceAvailableFilter.filter(requestContext));
            assertEquals("Service unavailable. System status: null", eNull.getMessage());
          },

          // then
          () -> {
            final Throwable eEmpty =
                assertThrows(
                    ServiceUnavailableException.class,
                    () ->
                        // when
                        serviceAvailableFilter.filter(requestContext));
            assertEquals("Service unavailable. System status: " + STATUS_NOK, eEmpty.getMessage());
          });
    }

    @Test
    @DisplayName(
        "Given exception thrown when querying system status, then throw ServiceUnavailableException")
    void givenExceptionThrownWhenQueryingSystemStatus_thenThrowServiceUnavailableException() {
      // given
      when(daoFactory.daoFor(Config.class)).thenThrow(new RuntimeException("Yup, we're testing"));

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
