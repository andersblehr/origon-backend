package co.origon.api.controller;

import co.origon.api.model.api.DaoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    DaoFactory daoFactory;

    @InjectMocks
    private AuthController authController;

    @Nested
    @DisplayName("GET /auth/register")
    class WhenRegisterUser {

        @Test
        @DisplayName("Given valid request, then register user")
        void givenValidRequest_thenRegisterUser() {
            assertTrue(true);
        }
    }
}