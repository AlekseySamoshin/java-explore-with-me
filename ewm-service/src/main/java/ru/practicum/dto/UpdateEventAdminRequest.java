package ru.practicum.dto;

import ru.practicum.model.Location;

public class UpdateEventAdminRequest {
    String annotation;
//    maxLength: 2000
//    minLength: 20

    Long category;


    String description;
//    maxLength: 7000
//    minLength: 20

    String eventDate;
//    example: 2023-10-11 23:10:05

    Location location;
    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    String stateAction;
//    Enum: PUBLISH_EVENT, REJECT_EVENT

    String title;
}
