package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import co.origon.api.common.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidBearerTokenFilterTest {

  private static final String AUTH_HEADER_INVALID_NO_OF_ELEMENTS =
      "This is not a valid Bearer token header";
  private static final String AUTH_HEADER_INVALID_SCHEME = "Basic user@example.com:password";
  private static final String AUTH_HEADER_INVALID_JWT_TOKEN = "Bearer thisisnotajwttoken";

  @Mock private ContainerRequestContext requestContext;

  private ValidBearerTokenFilter validBearerTokenFilter;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @BeforeEach
    void setUp() {
      validBearerTokenFilter = new ValidBearerTokenFilter();
    }

    @Test
    @DisplayName("Given valid credentials, then run to completion")
    void givenValidCredentials_thenRunToCompletion() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn("Bearer " + getJwtToken(1000, 60000));

      // when
      validBearerTokenFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given no Authorization header, then throw BadRequestException")
    void givenNoAuthorizationHeader_thenThrowBadRequestException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(null, "");

      assertAll(
          "Missing Authorization header",
          // then
          () -> {
            final Throwable eNull =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        validBearerTokenFilter.filter(requestContext));
            assertEquals("Missing Authorization header", eNull.getMessage());
          },

          // then
          () -> {
            final Throwable eEmpty =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        validBearerTokenFilter.filter(requestContext));
            assertEquals("Missing Authorization header", eEmpty.getMessage());
          });
    }

    @Test
    @DisplayName(
        "Given invalid number of elements in Authorization header, then throw BadRequestException")
    void givenInvalidNumberOfElementsInAuthorizationHeader_thenThrowBadRequestException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn(AUTH_HEADER_INVALID_NO_OF_ELEMENTS);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validBearerTokenFilter.filter(requestContext));
      assertEquals(
          "Invalid Authorization header: " + AUTH_HEADER_INVALID_NO_OF_ELEMENTS, e.getMessage());
    }

    @Test
    @DisplayName("Given invalid scheme in Authorization header, then throw BadRequestException")
    void givenInvalidSchemeInAuthorizationHeader_thenThrowBadRequestException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn(AUTH_HEADER_INVALID_SCHEME);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validBearerTokenFilter.filter(requestContext));
      assertEquals("Invalid authentication scheme for Bearer token: Basic", e.getMessage());
    }

    @Test
    @DisplayName("Given expired credentials, then throw NotAuthorizedException")
    void givenExpiredCredentials_thenThrowNotAuthorizedException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn("Bearer " + getJwtToken(60000, 1000));

      // then
      final WebApplicationException e =
          assertThrows(
              NotAuthorizedException.class,
              () ->
                  // when
                  validBearerTokenFilter.filter(requestContext));
      assertEquals("JWT token has expired", e.getMessage());
      final String authChallenge =
          e.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE).toString();
      assertEquals(ValidBearerTokenFilter.WWW_AUTH_CHALLENGE_BEARER_TOKEN, authChallenge);
    }

    @Test
    @DisplayName("Given invalid credentials, then throw BadRequestException")
    void givenInvalidCredentials_thenThrowBadRequestException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn(AUTH_HEADER_INVALID_JWT_TOKEN);

      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validBearerTokenFilter.filter(requestContext));
      assertEquals("Invalid JWT token", e.getMessage());
    }
  }

  private static String getJwtToken(int ageMillis, int validMillis) {
    final long issuedAtMillis = System.currentTimeMillis() - ageMillis;
    try {
      return JWT.create()
          .withIssuer(Config.jwt().getString(Config.JWT_ISSUER))
          .withIssuedAt(new Date(issuedAtMillis))
          .withExpiresAt(new Date(issuedAtMillis + validMillis))
          .sign(Algorithm.HMAC256(Config.jwt().getString(Config.JWT_SECRET)));
    } catch (UnsupportedEncodingException e) {
      fail(e);
      throw new RuntimeException(e);
    }
  }
}
