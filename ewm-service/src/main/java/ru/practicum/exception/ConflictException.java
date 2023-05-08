package ru.practicum.exception;

public class ConflictException extends RuntimeException {
    String reason;
    public ConflictException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
