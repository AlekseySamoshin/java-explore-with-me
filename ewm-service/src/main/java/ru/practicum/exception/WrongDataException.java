package ru.practicum.exception;

import lombok.Getter;

@Getter
public class WrongDataException extends RuntimeException {
    private String reason;

    public WrongDataException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
