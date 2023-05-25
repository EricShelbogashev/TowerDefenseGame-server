package ru.nsu.shelbogashev.tdgserver.server.exception;

public class GlobalServerException extends RuntimeException {
    public GlobalServerException() {
    }

    public GlobalServerException(String message) {
        super(message);
    }

    public GlobalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalServerException(Throwable cause) {
        super(cause);
    }

    public GlobalServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
