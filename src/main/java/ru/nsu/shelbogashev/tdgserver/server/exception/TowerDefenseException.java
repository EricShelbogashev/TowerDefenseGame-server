package ru.nsu.shelbogashev.tdgserver.server.exception;

import org.springframework.messaging.MessagingException;

public class TowerDefenseException extends MessagingException {
    public TowerDefenseException(String message) {
        super(message);
    }
}
