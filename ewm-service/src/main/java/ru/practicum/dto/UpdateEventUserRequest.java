package ru.practicum.dto;

import ru.practicum.model.Location;

public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    public String StateAction;
}
