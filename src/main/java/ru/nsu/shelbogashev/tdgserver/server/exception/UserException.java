package ru.nsu.shelbogashev.tdgserver.server.exception;

/**
 * Exception for Auth API section.
 */
public class UserException extends GlobalServerException {
    public UserException(String message) {
        super(message);
    }

    public UserException(Throwable cause) {
        super(cause);
    }
}
