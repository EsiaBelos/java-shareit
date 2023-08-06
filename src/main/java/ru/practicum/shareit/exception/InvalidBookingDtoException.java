package ru.practicum.shareit.exception;

public class InvalidBookingDtoException extends IllegalArgumentException {
    public InvalidBookingDtoException(String message) {
        super(message);
    }
}
