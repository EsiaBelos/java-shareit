package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class,
            AccessDeniedException.class, RequestNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler({InvalidBookingDtoException.class,
            IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleIllegalArgument(final IllegalArgumentException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(e.getMessage()));
    }

//    @ExceptionHandler
//    public ResponseEntity<Message> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
//        log.debug(e.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new Message(e.getMessage()));
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleThrowable(final Throwable e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(e.getMessage()));
    }
}
