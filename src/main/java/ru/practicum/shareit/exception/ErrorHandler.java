package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleInvalidEmail(final EmailException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class,ItemNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleUserNotFound(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }
}
