package ru.nsu.shelbogashev.tdgserver.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.MessageDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(Exception exception) {
        log.error("handleRuntimeException() : exception.getMessage()=" + exception.getMessage());
        exception.printStackTrace();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<MessageDto> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.info("handleHttpMediaTypeNotSupportedException() : exception.getMessage()=" + exception.getMessage());

        return ResponseEntity.status(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseFactory.toMessage(exception.getMessage())
        );
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<MessageDto> handleUserException(AuthException exception) {
        log.info("handleAuthException() : exception.getMessage()=" + exception.getMessage());

        return ResponseEntity.badRequest().body(ResponseFactory.toMessage(exception.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<MessageDto> handleUserException(UserException exception) {
        log.info("handleUserException() : exception.getMessage()=" + exception.getMessage());

        return ResponseEntity.badRequest().body(ResponseFactory.toMessage(exception.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<MessageDto> handleInternalServerException(InternalServerException exception) {
        log.info("handleInternalServerException() : exception.getMessage()=" + exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.toMessage(exception.getMessage()));
    }

    @ExceptionHandler(GlobalServerException.class)
    public ResponseEntity<MessageDto> handleGlobalExceptionHandler(GlobalServerException exception) {
        log.error("handleGlobalExceptionHandler() : exception.getMessage()=" + exception.getMessage());

        exception.printStackTrace();

        return ResponseEntity.badRequest().body(ResponseFactory.toMessage(ResponseMessage.UNEXPECTED_ERROR));
    }
//    private static final String FETCH_ERROR_MESSAGE = "/topic/error";
//    SimpMessagingTemplate messagingTemplate;
//
//    @SuppressWarnings("SameParameterValue")
//    private static ResponseEntity<Message> getMessageResponse(String message, HttpStatus status) {
//        Message response = new Message();
//        response.setMessage(message);
//        return new ResponseEntity<>(response, status);
//    }
//
//
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<Message> handleBadCredentialsException() {
//        return getMessageResponse(ResponseMessage.BAD_CREDENTIALS_ERROR, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Message> handleMethodArgumentNotValidExceptionException() {
//        return getMessageResponse(ResponseMessage.ILLEGAL_REQUEST_FORMAT_ERROR, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<Message> handleIllegalStateExceptionException(IllegalStateException exception) {
//        log.error(exception.getMessage());
//        return getMessageResponse(ResponseMessage.UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @MessageExceptionHandler(TowerDefenseException.class)
//    @SendToUser(FETCH_ERROR_MESSAGE)
//    public ResponseEntity<Message> handleTowerDefenseException(TowerDefenseException exception) {
//        return getMessageResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
//    }
}
