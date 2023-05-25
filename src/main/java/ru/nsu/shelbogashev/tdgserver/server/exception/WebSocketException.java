package ru.nsu.shelbogashev.tdgserver.server.exception;

/**
 * Exception for Auth API section.
 */
public class WebSocketException extends GlobalServerException {
    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(Throwable cause) {
        super(cause);
    }
}
