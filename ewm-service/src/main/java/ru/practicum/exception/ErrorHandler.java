package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleWrongData(final WrongDataException e) {
        log.error("Ошибка: " + e.getMessage());
        return ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace())
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.error("Ошибка: " + e.getMessage());
        return ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace())
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        log.error("Ошибка: " + e.getMessage());
        return ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace())
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError propertyValueException(final PropertyValueException e) {
        log.error("Ошибка: " + e.getMessage());
        return ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace())
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .message("ошибка валидации данных")
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
    }
}
