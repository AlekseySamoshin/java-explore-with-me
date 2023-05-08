package ru.practicum.exception;

import java.util.List;

public class WrongDataException extends RuntimeException {
    String reason;
    public WrongDataException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
