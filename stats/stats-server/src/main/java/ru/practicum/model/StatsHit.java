package ru.practicum.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
//@NoArgsConstructor
@Entity
@Table(name = "hits")
public class StatsHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "datetime", nullable = false)
    private LocalDateTime timestamp;

    @Override
    public int hashCode() {
        return Objects.hash(id, app, uri, ip, timestamp);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        StatsHit thisHit = (StatsHit) other;
        return id.equals(thisHit.id)
                && app.equals(thisHit.app)
                && uri.equals(thisHit.uri)
                && ip.equals(thisHit.ip)
                && timestamp.equals(thisHit.timestamp);
    }
}
