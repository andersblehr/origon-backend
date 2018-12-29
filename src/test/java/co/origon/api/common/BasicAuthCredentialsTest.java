package co.origon.api.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
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
            final BasicAuthCredentials credentials = BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS);
            assertEquals("user@example.com", credentials.getEmail());
            assertEquals("password", credentials.getPassword());
            assertEquals(Crypto.generatePasswordHash("password"), credentials.getPasswordHash());
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header is missing")
        void throwIllegalArgumentException_whenAuthorizationHeaderMissing() {
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
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header has too many elements")
        void throwIllegalArgumentException_whenAuthorizationHeaderHasTooManyElements() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_TOO_MANY_ELEMENTS)
            );
            assertTrue(e.getMessage().startsWith("Invalid Authorization header"));
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header has invalid scheme")
        void throwIllegalArgumentException_whenAuthorizationHeaderHasInvalidScheme() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_SCHEME)
            );
            assertTrue(e.getMessage().startsWith("Invalid authentication scheme for HTTP basic auth"));
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid base64 encoding")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidBase64Encoding() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_BASE64)
            );
            assertEquals("Credentials are not base 64 encoded", e.getMessage() );
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid credentials")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidCredentials() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_CREDENTIALS)
            );
            assertTrue(e.getMessage().startsWith("Invalid basic auth credentials"));
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid email address")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidEmailAddress() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_EMAIL)
            );
            assertTrue(e.getMessage().startsWith("Invalid email address"));
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when Authorization header contains invalid password")
        void throwIllegalArgumentException_whenAuthorizationHeaderContainsInvalidPassword() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(AUTH_HEADER_INVALID_PASSWORD)
            );
            assertTrue(e.getMessage().startsWith("Password is too short"));
            setCredentials(null);
        }
    }

    @Nested
    class GetCredentials {

        @Test
        @DisplayName("Retrieve credentials successfully when successfully validated")
        void retrieveCredentials_whenSuccessfullyValidated() {
            BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS);
            final BasicAuthCredentials credentials = BasicAuthCredentials.getCredentials();
            assertEquals("user@example.com", credentials.getEmail());
            assertEquals("password", credentials.getPassword());
            assertEquals(Crypto.generatePasswordHash("password"), credentials.getPasswordHash());
            setCredentials(null);
        }

        @Test
        @DisplayName("Retrieve null credentials when not successfully validated")
        void retrieveNullCredentials_whenNotSuccessfullyValidated() {
            assertThrows(IllegalArgumentException.class, () ->
                    BasicAuthCredentials.validate(null)
            );
            assertNull(BasicAuthCredentials.getCredentials());
            setCredentials(null);
        }

        @Test
        @DisplayName("Throw RuntimeException when credentials have already been validated")
        void throwRuntimeException_whenCredentialsHaveAlreadyBeenValidated() {
            setCredentials(BasicAuthCredentials.validate(AUTH_HEADER_VALID_CREDENTIALS));
            assertThrows(RuntimeException.class, () ->
                    BasicAuthCredentials.validate(null)
            );
            assertNotNull(BasicAuthCredentials.getCredentials());
            setCredentials(null);
        }
    }

    private static String b64encode(String credentials) {
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    private void setCredentials(BasicAuthCredentials credentials) {
        try {
            Field credentialsField = BasicAuthCredentials.class.getDeclaredField("credentials");
            credentialsField.setAccessible(true);
            credentialsField.set(null, credentials);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
