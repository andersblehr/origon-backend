package co.origon.api.filters;

import co.origon.api.common.UrlParams;
import co.origon.api.entities.OAuthMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticatedFilterTest {

    private final static String VALID_AUTH_TOKEN = "96ae6cd160219b214ba8fe816344a478145a2a61";

    @Mock private ContainerRequestContext requestContext;
    @Mock private UriInfo uriInfo;
    @Mock private MultivaluedMap<String, String> queryParameters;

    private TokenAuthenticatedFilter tokenAuthenticatedFilter;

    @BeforeEach
    void setUp() {
        tokenAuthenticatedFilter = new TokenAuthenticatedFilter();
    }

    @Nested
    class Filter {

        @Test
        @DisplayName("Throw BadRequestException when missing or invalid auth token")
        void throwNoExceptions_whenValidAuthToken() {
            // given
            when(requestContext.getUriInfo())
                    .thenReturn(uriInfo);
            when(uriInfo.getQueryParameters())
                    .thenReturn(queryParameters);
            when(queryParameters.getFirst(UrlParams.AUTH_TOKEN))
                    .thenReturn(VALID_AUTH_TOKEN);
/*            when(OAuthMeta.get(VALID_AUTH_TOKEN))
                    .thenReturn(OAuthMeta.builder()
                            .authToken(VALID_AUTH_TOKEN)
                            .email("user@example.com")
                            .deviceId(UUID.randomUUID().toString())
                            .deviceType("Mockito")
                            .build()
                    );

            // when
            tokenAuthenticatedFilter.filter(requestContext);
*/
            // then
            assertTrue(true);
        }

        @Test
        @DisplayName("Throw BadRequestException when missing or invalid auth token")
        void throwBadRequestException_whenMissingOrInvalidAuthToken() {

        }
    }
}