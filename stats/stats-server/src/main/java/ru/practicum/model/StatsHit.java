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
//     @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Override
    public int hashCode() {
        return Objects.hash(id, app, uri, ip, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatsHit thisEndpoint = (StatsHit) o;
        return id.equals(thisEndpoint.id)
                && app.equals(thisEndpoint.app)
                && uri.equals(thisEndpoint.uri)
                && ip.equals(thisEndpoint.ip)
                && timestamp.equals(thisEndpoint.timestamp);
    }
}
