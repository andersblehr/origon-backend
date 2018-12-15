package co.origon.api.exceptions;

public class IllegalCredentialsException extends RuntimeException {

    public IllegalCredentialsException(String message) {
        super(message);
    }

    public IllegalCredentialsException(Throwable cause) {
        super(cause);
    }

    public IllegalCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
