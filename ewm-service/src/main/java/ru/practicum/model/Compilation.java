package ru.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

//    @ManyToOne
//    @JoinTable(name = "events_to_compilations",
//            joinColumns = @JoinColumn(name = "compilation_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id"))
//            indexes = {
//            @Index(name = "idx_event_id", columnList = "event_id")
//            @Index(name = "idx_compilation_id", columnList = "compilation_id")
//    })
    @Transient
    private List<Long> events;

    private Boolean pinned;

    private String title;

    @ManyToOne
    @JoinTable(name = "events_to_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private void addEvent(Long eventId) {
        events.add(eventId);
    }
}
