package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.origon.api.common.BasicAuthCredentials;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidBasicAuthCredentialsFilterTest {

  private static final String VALID_CREDENTIALS = "Basic " + encode("user@example.com:password");
  private static final String INVALID_CREDENTIALS = encode("user@example.com:password");

  private ValidBasicAuthCredentialsFilter validBasicAuthCredentialsFilter =
      new ValidBasicAuthCredentialsFilter();

  @Mock private ContainerRequestContext requestContext;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @Test
    @DisplayName("Given valid credentials, then run to completion")
    void givenValidCredentials_thenRunToCompletion() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn(VALID_CREDENTIALS);
      // when
      validBasicAuthCredentialsFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given invalid credentials, then throw BadRequestException")
    void givenInvalidCredentials_thenThrowBadRequestException() {
      // given
      when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION))
          .thenReturn(INVALID_CREDENTIALS);
      // then
      final Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  validBasicAuthCredentialsFilter.filter(requestContext));
      assertEquals("Invalid basic auth credentials", e.getMessage());
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
