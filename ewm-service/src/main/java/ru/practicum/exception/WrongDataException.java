package ru.practicum.exception;

public class WrongDataException extends RuntimeException {
    String reason;

    public WrongDataException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
