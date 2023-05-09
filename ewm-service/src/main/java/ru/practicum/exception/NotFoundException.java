package ru.practicum.exception;

public class NotFoundException extends RuntimeException {

    String reason;

    public NotFoundException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
