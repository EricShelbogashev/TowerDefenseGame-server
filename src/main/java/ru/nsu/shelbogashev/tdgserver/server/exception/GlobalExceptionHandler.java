package ru.nsu.shelbogashev.tdgserver.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info("handleMethodArgumentNotValidException() : exception.getMessage()=" + exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseFactory.toMessage(ResponseMessage.ILLEGAL_REQUEST_FORMAT_ERROR));
    }

    @ExceptionHandler(TowerDefenseException.class)
    public ResponseEntity<MessageDto> handleTowerDefenseException(TowerDefenseException exception) {
        log.error("handleTowerDefenseException() : exception.getMessage()=" + exception.getMessage());

        exception.printStackTrace();

        return ResponseEntity.badRequest().body(ResponseFactory.toMessage(exception.getMessage()));
    }

    @ExceptionHandler(GlobalServerException.class)
    public ResponseEntity<MessageDto> handleGlobalExceptionHandler(GlobalServerException exception) {
        log.error("handleGlobalExceptionHandler() : exception.getMessage()=" + exception.getMessage());

        exception.printStackTrace();

        return ResponseEntity.badRequest().body(ResponseFactory.toMessage(ResponseMessage.UNEXPECTED_ERROR));
    }
}
