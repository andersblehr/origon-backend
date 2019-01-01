package co.origon.api.filter;

import co.origon.api.model.api.Dao;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.Config.Category;
import co.origon.api.model.api.entity.Config.Setting;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticatedFilterTest {

    private static final String AUTH_HEADER_INVALID_NO_OF_ELEMENTS = "This is not a valid Bearer token header";
    private static final String AUTH_HEADER_INVALID_SCHEME = "Basic user@example.com:password";
    private static final String AUTH_HEADER_INVALID_JWT_TOKEN = "Bearer thisisnotajwttoken";

    private static final String JWT_ISSUER = "issuer";
    private static final String JWT_SECRET = "secret";

    private JwtAuthenticatedFilter jwtAuthenticatedFilter;

    @Mock private DaoFactory daoFactory;
    @Mock private Dao<Config> configDao;
    @Mock private Config jwtConfig;
    @Mock private ContainerRequestContext requestContext;

    @Nested
    class WhenFilter {

        @BeforeEach
        void setUp() {
            jwtAuthenticatedFilter = new JwtAuthenticatedFilter(daoFactory);
        }

        @Test
        @DisplayName("Given valid credentials, then run to completion")
        void givenValidCredentials_thenRunToCompletion() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn("Bearer " + getJwtToken(1000, 60000));
            when(daoFactory.daoFor(Config.class))
                    .thenReturn(configDao);
            when(configDao.get(Category.JWT))
                    .thenReturn(jwtConfig);
            when(jwtConfig.getString(Setting.SECRET))
                    .thenReturn(JWT_SECRET);

            // when
            jwtAuthenticatedFilter.filter(requestContext);

            // then
            assertTrue(true);
        }

        @Test
        @DisplayName("Given no Authorization header, then throw BadRequestException")
        void givenNoAuthorizationHeader_thenThrowBadRequestException() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn(null, "");

            assertAll("Missing Authorization header",
                    // then
                    () -> {
                        final Throwable eNull = assertThrows(BadRequestException.class, () ->
                                // when
                                jwtAuthenticatedFilter.filter(requestContext)
                        );
                        assertEquals("Missing Authorization header", eNull.getMessage());
                    },

                    // then
                    () -> {
                        final Throwable eEmpty = assertThrows(BadRequestException.class, () ->
                                // when
                                jwtAuthenticatedFilter.filter(requestContext)
                        );
                        assertEquals("Missing Authorization header", eEmpty.getMessage());
                    }
            );
        }

        @Test
        @DisplayName("Given invalid number of elements in Authorization header, then throw BadRequestException")
        void givenInvalidNumberOfElementsInAuthorizationHeader_thenThrowBadRequestException() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn(AUTH_HEADER_INVALID_NO_OF_ELEMENTS);

            // then
            final Throwable e = assertThrows(BadRequestException.class, () ->
                    // when
                    jwtAuthenticatedFilter.filter(requestContext)
            );
            assertEquals("Invalid Authorization header: " + AUTH_HEADER_INVALID_NO_OF_ELEMENTS, e.getMessage());
        }

        @Test
        @DisplayName("Given invalid scheme in Authorization header, then throw BadRequestException")
        void givenInvalidSchemeInAuthorizationHeader_thenThrowBadRequestException() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn(AUTH_HEADER_INVALID_SCHEME);

            // then
            final Throwable e = assertThrows(BadRequestException.class, () ->
                    // when
                    jwtAuthenticatedFilter.filter(requestContext)
            );
            assertEquals("Invalid authentication scheme for Bearer token: Basic", e.getMessage());
        }

        @Test
        @DisplayName("Given expired credentials, then throw NotAuthorizedException")
        void givenExpiredCredentials_thenThrowNotAuthorizedException() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn("Bearer " + getJwtToken(60000, 1000));
            when(daoFactory.daoFor(Config.class))
                    .thenReturn(configDao);
            when(configDao.get(Category.JWT))
                    .thenReturn(jwtConfig);
            when(jwtConfig.getString(Setting.SECRET))
                    .thenReturn(JWT_SECRET);

            // then
            final WebApplicationException e = assertThrows(NotAuthorizedException.class, () ->
                    // when
                    jwtAuthenticatedFilter.filter(requestContext)
            );
            assertEquals("JWT token has expired", e.getMessage());
            final String authChallenge = e.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE).toString();
            assertEquals(JwtAuthenticatedFilter.WWW_AUTHENTICATE_CHALLENGE_BEARER_TOKEN, authChallenge);
        }

        @Test
        @DisplayName("Given invalid credentials, then throw BadRequestException")
        void givenInvalidCredentials_thenThrowBadRequestException() {
            // given
            when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
                    .thenReturn(AUTH_HEADER_INVALID_JWT_TOKEN);
            when(daoFactory.daoFor(Config.class))
                    .thenReturn(configDao);
            when(configDao.get(Category.JWT))
                    .thenReturn(jwtConfig);
            when(jwtConfig.getString(Setting.SECRET))
                    .thenReturn(JWT_SECRET);

            // then
            final Throwable e = assertThrows(BadRequestException.class, () ->
                    // when
                    jwtAuthenticatedFilter.filter(requestContext)
            );
            assertEquals("Invalid JWT token", e.getMessage());
        }
    }

    private static String getJwtToken(int ageMillis, int validMillis) {
        final long issuedAtMillis = System.currentTimeMillis() - ageMillis;
        try {
            return JWT.create()
                    .withIssuer(JWT_ISSUER)
                    .withIssuedAt(new Date(issuedAtMillis))
                    .withExpiresAt(new Date(issuedAtMillis + validMillis))
                    .sign(Algorithm.HMAC256(JWT_SECRET));
        } catch (UnsupportedEncodingException e) {
            fail(e);
            throw new RuntimeException(e);
        }
    }
}