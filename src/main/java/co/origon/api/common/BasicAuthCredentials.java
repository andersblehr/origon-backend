package co.origon.api.common;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class BasicAuthCredentials {
    private static ThreadLocal<BasicAuthCredentials> localCredentials;
    private static final int LENGTH_PASSWORD_MIN = 6;

    private final String email;
    private final String password;
    private final String passwordHash;

    public static BasicAuthCredentials validate(String authorizationHeader) {
        if (localCredentials != null)
            throw new RuntimeException("Basic auth credentials have already been validated");

        final BasicAuthCredentials credentials = new BasicAuthCredentials(authorizationHeader);
        localCredentials = ThreadLocal.withInitial(() -> credentials);
        return localCredentials.get();
    }

    public static BasicAuthCredentials getCredentials() {
        if (localCredentials == null)
            throw new RuntimeException("No basic auth credentials have been validated, cannot get");
        return localCredentials.get();
    }

    public static void dispose() {
        localCredentials = null;
    }

    private BasicAuthCredentials(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.length() == 0)
            throw new IllegalArgumentException("Missing Authorization header");

        final String[] authElements = authorizationHeader.split(" ");
        if (authElements.length != 2)
            throw new IllegalArgumentException("Invalid Authorization header: " + authorizationHeader);
        if (!authElements[0].equals("Basic"))
            throw new IllegalArgumentException("Invalid authentication scheme for HTTP basic auth: " + authElements[0]);

        final String credentialsString;
        try {
            credentialsString = Base64.decode(authElements[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("DeviceCredentials are not base 64 encoded");
        }

        final String[] credentials = credentialsString.split(":");
        if (credentials.length != 2)
            throw new IllegalArgumentException("Invalid basic auth credentials: " + credentialsString);
        if (!credentials[0].matches("^.+@.+\\..+$"))
            throw new IllegalArgumentException("Invalid email address: " + credentials[0]);
        if (credentials[1].length() < LENGTH_PASSWORD_MIN)
            throw new IllegalArgumentException("Password is too short: " + credentials[1]);

        email = credentials[0];
        password = credentials[1];
        passwordHash = Crypto.generatePasswordHash(password);
    }
}
