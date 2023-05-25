package ru.nsu.shelbogashev.tdgserver.server.exception;

/**
 * Exception for Auth API section.
 */
public class AuthException extends GlobalServerException {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}
