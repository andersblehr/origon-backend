package co.origon.api.filter;

import co.origon.api.common.UrlParams;
import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.DeviceCredentials;
import co.origon.api.model.api.entity.MemberProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticatedFilterTest {

    private final static String VALID_DEVICE_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";
    private final static String VALID_EMAIL = "user@example.com";

    @Mock private ContainerRequestContext requestContext;
    @Mock private UriInfo uriInfo;
    @Mock private MultivaluedMap<String, String> queryParameters;
    @Mock private DaoFactory daoFactory;
    @Mock private Dao<DeviceCredentials> tokenCredentialsDao;
    @Mock private DeviceCredentials tokenCredentials;
    @Mock private Dao<MemberProxy> memberProxyDao;
    @Mock private MemberProxy memberProxy;

    @InjectMocks
    private TokenAuthenticatedFilter tokenAuthenticatedFilter;

    @BeforeEach
    void setUp() {
        tokenAuthenticatedFilter = new TokenAuthenticatedFilter();
    }

    @Nested
    class Filter {

        @Test
        @DisplayName("Throw no exceptions when device token is valid")
        void throwNoExceptions_whenValidDeviceToken() {
            // given
            when(requestContext.getUriInfo())
                    .thenReturn(uriInfo);
            when(uriInfo.getQueryParameters())
                    .thenReturn(queryParameters);
            when(queryParameters.getFirst(UrlParams.DEVICE_TOKEN))
                    .thenReturn(VALID_DEVICE_TOKEN);
            lenient().when(daoFactory.daoFor(DeviceCredentials.class))
                    .thenReturn(tokenCredentialsDao);
            when(tokenCredentialsDao.get(VALID_DEVICE_TOKEN))
                    .thenReturn(tokenCredentials);
            when(tokenCredentials.dateExpires())
                    .thenReturn(new Date(System.currentTimeMillis() + 1000L));
            when(tokenCredentials.email())
                    .thenReturn(VALID_EMAIL);
            lenient().when(daoFactory.daoFor(MemberProxy.class))
                    .thenReturn(memberProxyDao);
            when(memberProxyDao.get(VALID_EMAIL))
                    .thenReturn(memberProxy);
            when(memberProxy.isRegistered())
                    .thenReturn(true);

            // when
            tokenAuthenticatedFilter.filter(requestContext);

            // then
            assertTrue(true);
        }

        @Test
        @DisplayName("Throw BadRequestException when missing or invalid device token")
        void throwBadRequestException_whenMissingOrInvalidDeviceToken() {

        }
    }
}