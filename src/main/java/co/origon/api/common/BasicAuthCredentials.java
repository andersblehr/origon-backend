package co.origon.api.common;

import lombok.Getter;

import java.util.Base64;

@Getter
public class BasicAuthCredentials {
    private static final int LENGTH_PASSWORD_MIN = 6;

    private static BasicAuthCredentials credentials;

    private final String email;
    private final String password;
    private final String passwordHash;

    public static BasicAuthCredentials validate(String authorizationHeader) {
        credentials = null;
        return credentials = new BasicAuthCredentials(authorizationHeader);
    }

    public static BasicAuthCredentials getCredentials() {
        return credentials;
    }

    private BasicAuthCredentials(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.length() == 0) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        final String[] authElements = authorizationHeader.split(" ");
        if (authElements.length != 2) {
            throw new IllegalArgumentException("Invalid Authorization header: " + authorizationHeader);
        }
        if (!authElements[0].equals("Basic")) {
            throw new IllegalArgumentException("Invalid authentication scheme for HTTP basic auth: " + authElements[0]);
        }

        final String credentialsString;
        try {
            credentialsString = new String(Base64.getDecoder().decode(authElements[1].getBytes()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Credentials are not base 64 encoded");
        }

        final String[] credentials = credentialsString.split(":");
        if (credentials.length != 2) {
            throw new IllegalArgumentException("Invalid basic auth credentials: " + credentialsString);
        }
        if (!credentials[0].matches("^.+@.+\\..+$")) {
            throw new IllegalArgumentException("Invalid email address: " + credentials[0]);
        }

        if (credentials[1].length() < LENGTH_PASSWORD_MIN) {
            throw new IllegalArgumentException("Password is too short: " + credentials[1]);
        }

        email = credentials[0];
        password = credentials[1];
        passwordHash = Crypto.generatePasswordHash(password);
    }
}