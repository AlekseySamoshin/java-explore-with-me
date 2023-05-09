package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events" )
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 2000)
    String annotation;

    @ManyToOne(optional = false)
    @JoinTable(name = "events_to_categories",
            joinColumns = @JoinColumn(name = "event_id" ),
            inverseJoinColumns = @JoinColumn(name = "category_id" ))
    Category category;

    Long confirmedRequests;

    @Column(name = "created_on" )
    LocalDateTime createdOn;

    @Column(length = 7000)
    String description;

    @Column(nullable = false, name = "event_date" )
    LocalDateTime eventDate;

    @ManyToOne(optional = false)
    User initiator;

    @ManyToOne(optional = false)
    Location location;

    @Column(nullable = false)
    Boolean paid;

    @Column(name = "participant_limit" )
    Integer participantLimit;

    @Column(name = "published_on" )
    LocalDateTime publishedOn;

    @Column(name = "request_moderation" )
    Boolean requestModeration;

    EventState state;

    @Column(nullable = false)
    String title;

    @ManyToMany
    @JoinTable(name = "events_to_compilations",
            joinColumns = @JoinColumn(name = "compilation_id" ),
            inverseJoinColumns = @JoinColumn(name = "event_id" ))
    List<Compilation> compilationList;
}
