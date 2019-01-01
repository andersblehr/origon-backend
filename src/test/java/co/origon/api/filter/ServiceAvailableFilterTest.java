package co.origon.api.filter;

import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.Config.Category;
import co.origon.api.model.api.entity.Config.Setting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.ContainerRequestContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceAvailableFilterTest {

    private static final String STATUS_OK = "OK";
    private static final String STATUS_NOK = "testing";

    private ServiceAvailableFilter serviceAvailableFilter;

    @Mock private DaoFactory daoFactory;
    @Mock private Dao<Config> configDao;
    @Mock private Config systemConfig;
    @Mock private ContainerRequestContext requestContext;

    @Nested
    class WhenFilter {

        @BeforeEach
        void setUp() {
            serviceAvailableFilter = new ServiceAvailableFilter(daoFactory);
        }

        @Test
        @DisplayName("Given system status is OK, then run to completion")
        void givenSystemStatusIsOk_thenRunToCoompletion() {
            // given
            when(daoFactory.daoFor(Config.class))
                    .thenReturn(configDao);
            when(configDao.get(Category.SYSTEM))
                    .thenReturn(systemConfig);
            when(systemConfig.getString(Setting.STATUS))
                    .thenReturn(STATUS_OK);

            // when
            serviceAvailableFilter.filter(requestContext);

            // then
            assertTrue(true);
        }

        @Test
        @DisplayName("Given available but nok OK system status, then throw ServiceUnavailableException")
        void givenAvailableButNotOkSystemStatus_thenThrowServiceUnavailableException() {
            // given
            when(daoFactory.daoFor(Config.class))
                    .thenReturn(configDao);
            when(configDao.get(Category.SYSTEM))
                    .thenReturn(systemConfig);
            when(systemConfig.getString(Setting.STATUS))
                    .thenReturn(null, STATUS_NOK);

            assertAll("System status not OK or null",
                    // then
                    () -> {
                        final Throwable eNull = assertThrows(ServiceUnavailableException.class, () ->
                                // when
                                serviceAvailableFilter.filter(requestContext)
                        );
                        assertEquals("Service unavailable. System status: null", eNull.getMessage());
                    },

                    // then
                    () -> {
                        final Throwable eEmpty = assertThrows(ServiceUnavailableException.class, () ->
                                // when
                                serviceAvailableFilter.filter(requestContext)
                        );
                        assertEquals("Service unavailable. System status: " + STATUS_NOK, eEmpty.getMessage());
                    }
            );
        }

        @Test
        @DisplayName("Given exception thrown when querying system status, then throw ServiceUnavailableException")
        void givenExceptionThrownWhenQueryingSystemStatus_thenThrowServiceUnavailableException() {
            // given
            when(daoFactory.daoFor(Config.class))
                    .thenThrow(new RuntimeException("Yup, we're testing"));

            // then
            Throwable e = assertThrows(ServiceUnavailableException.class, () ->
                    // when
                    serviceAvailableFilter.filter(requestContext)
            );
            assertEquals("System status unavailable: Yup, we're testing", e.getMessage());
        }
    }
}