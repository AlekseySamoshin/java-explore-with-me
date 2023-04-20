package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.StatsHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatsHit, Long> {
    public List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    public List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
