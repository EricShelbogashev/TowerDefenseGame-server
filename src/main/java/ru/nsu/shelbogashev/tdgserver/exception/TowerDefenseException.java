package ru.nsu.shelbogashev.tdgserver.exception;

import org.springframework.messaging.MessagingException;

public class TowerDefenseException extends MessagingException {
    public TowerDefenseException(String message) {
        super(message);
    }
}
