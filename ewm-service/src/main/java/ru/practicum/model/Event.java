package ru.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.UserShortDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne
    @JoinTable(name = "categories")
    Category category;

    Long confirmedRequests;

    @Column(name = "created_on")
    String createdOn;

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne
    @JoinTable(name = "users")
    User initiator;

    @ManyToOne
    @JoinTable(name = "locations")
    Location location;

    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    String publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    EventState state;

    String title;

    @ManyToMany
    @JoinTable(name = "events_to_compilations",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id"))
    List<Compilation> compilationList;
}
