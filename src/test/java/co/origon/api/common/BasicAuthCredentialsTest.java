package co.origon.api.common;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BasicAuthCredentialsTest {

  private static final String AUTH_HEADER_VALID_CREDENTIALS =
      "Basic " + encode("user@example.com:password");
  private static final String AUTH_HEADER_INVALID_NO_OF_ELEMENTS =
      "This is not a valid basic auth header";
  private static final String AUTH_HEADER_INVALID_SCHEME = "Bearer user@example.com:password";
  private static final String AUTH_HEADER_INVALID_BASE64 = "Basic user@example.com:password";
  private static final String AUTH_HEADER_INVALID_CREDENTIALS =
      "Basic " + encode("user@example.com;password");
  private static final String AUTH_HEADER_INVALID_EMAIL =
      "Basic " + encode("user(at)example.com:password");
  private static final String AUTH_HEADER_INVALID_PASSWORD =
      "Basic " + encode("user@example.com:pwd");

  @Nested
  @DisplayName("validate()")
  class WhenValidate {

    @Test
    @DisplayName("Given valid credentials, then validate successfully")
    void givenValidCredentials_thenValidateSuccessfully() {
      final BasicAuthCredentials credentials =
          new BasicAuthCredentials(AUTH_HEADER_VALID_CREDENTIALS);
      assertEquals("user@example.com", credentials.email());
      assertEquals("password", credentials.password());
      assertEquals(
          BasicAuthCredentials.generatePasswordHash("password"), credentials.passwordHash());
    }

    @Test
    @DisplayName("Given no Authorization header, then throw IllegalArgumentException")
    void givenNoAuthorizationHeader_thenThrowIllegalArgumentException() {
      assertAll(
          "Missing Authorization header",
          () -> {
            Throwable e =
                assertThrows(IllegalArgumentException.class, () -> new BasicAuthCredentials(null));
            assertEquals("Missing Authorization header", e.getMessage());
          },
          () -> {
            Throwable e =
                assertThrows(IllegalArgumentException.class, () -> new BasicAuthCredentials(""));
            assertEquals("Missing Authorization header", e.getMessage());
          });
    }

    @Test
    @DisplayName(
        "Given invalid number of elements in Authorization header, then throw IllegalArgumentException")
    void givenInvalidNumberOfElementsInAuthorizationHeader_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_NO_OF_ELEMENTS));
      assertTrue(e.getMessage().startsWith("Invalid Authorization header"));
    }

    @Test
    @DisplayName(
        "Given invalid scheme in Authorization header, then throw IllegalArgumentException")
    void givenInvalidSchemeInAuthorizationHeader_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_SCHEME));
      assertTrue(e.getMessage().startsWith("Invalid authentication scheme for HTTP basic auth"));
    }

    @Test
    @DisplayName("Given invalid base64 string, then throw IllegalArgumentException")
    void givenInvalidBase64String_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_BASE64));
      assertEquals("DeviceCredentials are not base 64 encoded", e.getMessage());
    }

    @Test
    @DisplayName("Given invalid basic auth credentials, then throw IllegalArgumentException")
    void givenInvalidBasicAuthCredentials_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_CREDENTIALS));
      assertTrue(e.getMessage().startsWith("Invalid basic auth credentials"));
    }

    @Test
    @DisplayName("Given invalid email address, then throw IllegalArgumentException")
    void givenInvalidEmailAddress_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_EMAIL));
      assertTrue(e.getMessage().startsWith("Invalid email address"));
    }

    @Test
    @DisplayName("Given invalid password, then throw IllegalArgumentException")
    void givenInvalidPassword_thenThrowIllegalArgumentException() {
      Throwable e =
          assertThrows(
              IllegalArgumentException.class,
              () -> new BasicAuthCredentials(AUTH_HEADER_INVALID_PASSWORD));
      assertTrue(e.getMessage().startsWith("Password is too short"));
    }
  }

  @Nested
  @DisplayName("getCredentials()")
  class WhenGetCredentials {

    @Test
    @DisplayName("Given successfully validated credentials, then retrieve credentials successfully")
    void givenSuccessfullyValidatedCredentials_thenRetrieveCredentialsSuccessfully() {
      final BasicAuthCredentials credentials =
          new BasicAuthCredentials(AUTH_HEADER_VALID_CREDENTIALS);
      assertEquals("user@example.com", credentials.email());
      assertEquals("password", credentials.password());
      assertEquals(
          BasicAuthCredentials.generatePasswordHash("password"), credentials.passwordHash());
    }
  }

  private static String encode(String toBeEncoded) {
    return new String(java.util.Base64.getEncoder().encode(toBeEncoded.getBytes()));
  }
}
