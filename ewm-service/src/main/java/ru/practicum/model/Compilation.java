package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Transient
    @Column(nullable = false)
    private List<Long> events;

    private Boolean pinned;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinTable(name = "events_to_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private void addEvent(Long eventId) {
        events.add(eventId);
    }
}
