package ru.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.UserShortDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
//    Long category;
    Category category;
    Long confirmedRequests;
    String createdOn;
    String description;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}
