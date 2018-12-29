package co.origon.api.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthCredentialsTest {

    private static final String AUTH_HEADER_VALID_CREDENTIALS = "Basic " + b64encode("user@example.com:password");
    private static final String AUTH_HEADER_TOO_MANY_ELEMENTS = "Basic user@example.com password";
    private static final String AUTH_HEADER_INVALID_SCHEME = "Bearer user@example.com:password";
    private static final String AUTH_HEADER_INVALID_BASE64 = "Basic user@example.com:password";
    private static final String AUTH_HEADER_INVALID_CREDENTIALS = "Basic " + b64encode("user@example.com;password");
    private static final String AUTH_HEADER_INVALID_EMAIL = "Basic " + b64encode("user(at)example.com:password");
    private static final String AUTH_HEADER_INVALID_PASSWORD = "Basic " + b64encode("user@example.com:pwd");

    @Nested
    class Validate {

        @Test
        @DisplayName("Validate successfully when Authorization header contains valid credentials")
        void validateSuccessfully_whenAuthorizationHeaderContainsValidCredentials() {
            BasicAuthCredentials.credentials = null;
            final BasicAuthCredentials credentials = BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS);
            assertEquals("user@example.com", credentials.getEmail());
            assertEquals("password", credentials.getPassword());
            assertEquals(Crypto.generatePasswordHash("password"), credentials.getPasswordHash());
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header is missing")
        void throwIllegalArgumentException_whenAuthorizationHeaderMissing() {
            BasicAuthCredentials.credentials = null;
            assertAll("Missing Authorization header",
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                BasicAuthCredentials.validate(null)
                        );
                        assertEquals("Missing Authorization header", e.getMessage());
                    },
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                BasicAuthCredentials.validate("")
                        );
                        assertEquals("Missing Authorization header", e.getMessage());
                    }
            );
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header has too many elements")
        void throwIllegalArgumentException_whenAuthorizationHeaderHasTooManyElements() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_TOO_MANY_ELEMENTS)
            );
            assertTrue(e.getMessage().startsWith("Invalid Authorization header"));
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header has invalid scheme")
        void throwIllegalArgumentException_whenAuthorizationHeaderHasInvalidScheme() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_SCHEME)
            );
            assertTrue(e.getMessage().startsWith("Invalid authentication scheme for HTTP basic auth"));
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid base64 encoding")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidBase64Encoding() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_BASE64)
            );
            assertEquals("Credentials are not base 64 encoded", e.getMessage() );
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid credentials")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidCredentials() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_CREDENTIALS)
            );
            assertTrue(e.getMessage().startsWith("Invalid basic auth credentials"));
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid email address")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidEmailAddress() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_EMAIL)
            );
            assertTrue(e.getMessage().startsWith("Invalid email address"));
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid password")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidPassword() {
            BasicAuthCredentials.credentials = null;
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_PASSWORD)
            );
            assertTrue(e.getMessage().startsWith("Password is too short"));
        }
    }

    @Nested
    class GetCredentials {

        @Test
        @DisplayName("Retrieve credentials successfully when successfully validated")
        void retrieveCredentials_whenSuccessfullyValidated() {
            BasicAuthCredentials.credentials = null;
            BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS);
            final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
            assertEquals("user@example.com", credentials.getEmail());
            assertEquals("password", credentials.getPassword());
            assertEquals(Crypto.generatePasswordHash("password"), credentials.getPasswordHash());
        }

        @Test
        @DisplayName("Retrieve null credentials when not successfully validated")
        void retrieveNullCredentials_whenNotSuccessfullyValidated() {
            BasicAuthCredentials.credentials = null;
            assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(null)
            );
            assertNull(BasicAuthCredentials.getCredentials());
        }

        @Test
        @DisplayName("Throw RuntimeException when credentials have already been validated")
        void throwRuntimeException_whenCredentialsHaveAlreadyBeenValidated() {
            if (BasicAuthCredentials.credentials == null) {
                BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS);
            }
            assertThrows(RuntimeException.class, () ->
                    BasicAuthCredentials.validate(null)
            );
            assertNotNull(BasicAuthCredentials.getCredentials());
        }
    }

    private static String b64encode(String credentials) {
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }
}
