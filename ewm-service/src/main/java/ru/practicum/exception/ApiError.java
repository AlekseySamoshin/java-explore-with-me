package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiError {
    String message;
    String reason;
    String status;
    String timestamp;
    List<String> errors;
}