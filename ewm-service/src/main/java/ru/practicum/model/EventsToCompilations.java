package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events_to_compilations")
public class EventsToCompilations {
    @Id
    Long id;

//    @ManyToOne
    @JoinTable(name = "compilations")
    @JoinColumn(name = "id")
    @Column(name = "compilation_id")
    Long compilationId;

//    @ManyToOne
    @JoinTable(name = "events")
    @JoinColumn(name = "id")
    @Column(name = "event_id")
    Long eventId;
}
