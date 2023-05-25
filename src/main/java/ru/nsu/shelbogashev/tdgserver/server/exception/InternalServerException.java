package ru.nsu.shelbogashev.tdgserver.server.exception;

/**
 * Exception for Auth API section.
 */
public class InternalServerException extends GlobalServerException {
    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }
}
