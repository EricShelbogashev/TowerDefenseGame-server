package ru.nsu.shelbogashev.tdgserver.server.exception;

public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
        super(message);
    }
}