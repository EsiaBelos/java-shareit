package ru.practicum.shareit.exception;

public class ErrorMessage {
    private final String error;

    public ErrorMessage(String message) {
        this.error = message;
    }

    public String getError() {
        return error;
    }
}
