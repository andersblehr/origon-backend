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
        @DisplayName("Given valid session data, then create session")
        void givenValideSessionData_thenCreateSession() {
            final Session session = Session.create(SOME_UUID, "Device", "1.0");
            assertNotNull(session);
            Session.dispose();
        }

        @Test
        @DisplayName("Given invalid session data, then throw IllegalArgumentException")
        void givenInvalidSessionData_thenThrowIllegalArgumentException() {
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
        @DisplayName("Given existing session, then throw RuntimeException")
        void givenExistingSession_thenThrowRuntimeException() {
            Session.create(SOME_UUID, "Some device", "1.0");
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
        @DisplayName("Given successfully created session, then Retrieve session successfully")
        void givenSuccessfullyCreatedSession_thenRetrieveSessionSuccessfully() {
            Session.create(SOME_UUID, "Device", "1.0");
            assertNotNull(Session.getSession());
            Session.dispose();
        }

        @Test
        @DisplayName("Given failed session creation, then throw RuntimeException")
        void givenFailedSessionCreation_thenThrowRuntimeException() {
            assertThrows(IllegalArgumentException.class, () ->
                    Session.create(null, "Device", "1.0")
            );
            assertThrows(RuntimeException.class, Session::getSession);
            Session.dispose();
        }
    }
}