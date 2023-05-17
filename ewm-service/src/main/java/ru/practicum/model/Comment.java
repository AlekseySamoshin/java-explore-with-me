package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false, length = 2048)
    private String text;

    @ManyToOne
    private Event event;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private LocalDateTime created;

    private LocalDateTime updated;
}
