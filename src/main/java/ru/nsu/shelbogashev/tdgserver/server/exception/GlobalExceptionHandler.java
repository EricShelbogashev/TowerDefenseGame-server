package ru.nsu.shelbogashev.tdgserver.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String FETCH_ERROR_MESSAGE = "/topic/error";
    SimpMessagingTemplate messagingTemplate;

    @SuppressWarnings("SameParameterValue")
    private static ResponseEntity<Message> getMessageResponse(String message, HttpStatus status) {
        Message response = new Message();
        response.setMessage(message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Message> handleRuntimeException(Exception exception) {
        return getMessageResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Message> handleBadCredentialsException() {
        return getMessageResponse(ResponseMessage.BAD_CREDENTIALS_ERROR, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Message> handleMethodArgumentNotValidExceptionException() {
        return getMessageResponse(ResponseMessage.ILLEGAL_REQUEST_FORMAT_ERROR, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Message> handleIllegalStateExceptionException(IllegalStateException exception) {
        log.error(exception.getMessage());
        return getMessageResponse(ResponseMessage.UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @MessageExceptionHandler(TowerDefenseException.class)
    @SendToUser(FETCH_ERROR_MESSAGE)
    public ResponseEntity<Message> handleTowerDefenseException(TowerDefenseException exception) {
        return getMessageResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
