package co.origon.api.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    private static final String SOME_UUID = UUID.randomUUID().toString();

    @Nested
    class Create {

        @Test
        @DisplayName("Instantiate session when session data is valid")
        void instantiateSession_whenSessionDataValid() {
            final Session session = Session.create(SOME_UUID, "Device", "1.0");
            assertNotNull(session);
            Session.dispose();
        }

        @Test
        @DisplayName("Throw IllegalArgumentException when session data is missing or invalid")
        void throwIllegalArgumentException_whenSessionDataMissingOrInvalid() {
            assertAll("Invalid session data",
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                Session.create("Not a UUID", "Device", "1.0")
                        );
                        assertTrue(e.getMessage().startsWith("Invalid or missing parameter: " + UrlParams.DEVICE_ID));
                    },
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                Session.create(SOME_UUID, null, "1.0")
                        );
                        assertEquals("Missing parameter: " + UrlParams.DEVICE_TYPE, e.getMessage());
                    },
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                Session.create(SOME_UUID, "", "1.0")
                        );
                        assertEquals("Missing parameter: " + UrlParams.DEVICE_TYPE, e.getMessage());
                    },
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                Session.create(SOME_UUID, "Device", null)
                        );
                        assertEquals("Missing parameter: " + UrlParams.APP_VERSION, e.getMessage());
                    },
                    () -> {
                        Throwable e = assertThrows(IllegalArgumentException.class, () ->
                                Session.create(SOME_UUID, "Device", "")
                        );
                        assertEquals("Missing parameter: " + UrlParams.APP_VERSION, e.getMessage());
                    }
            );
            Session.dispose();
        }

        @Test
        @DisplayName("Throw RuntimeException when session has already been created")
        void throwRuntimeException_whenSessionHasAlreadyBeenCreated() {
            Session.create(SOME_UUID, "Device", "1.0");
            assertThrows(RuntimeException.class, () ->
                    Session.create(null, null, null)
            );
            assertNotNull(Session.getSession());
            Session.dispose();
        }
    }

    @Nested
    class GetSession {

        @Test
        @DisplayName("Retrieve session successfully when successfully created")
        void retrieveSession_whenSuccessfullyCreated() {
            Session.create(SOME_UUID, "Device", "1.0");
            assertNotNull(Session.getSession());
            Session.dispose();
        }

        @Test
        @DisplayName("Retrieve null session when not successfully created")
        void retrieveNullSession_whenNotSuccessfullyCreated() {
            assertThrows(IllegalArgumentException.class, () ->
                    Session.create(null, "Device", "1.0")
            );
            assertNull(Session.getSession());
            Session.dispose();
        }
    }
}