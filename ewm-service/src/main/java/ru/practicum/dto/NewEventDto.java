package ru.practicum.dto;

import ru.practicum.model.Location;

public class NewEventDto {
    String annotation;
    Long category;
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String title;
}
