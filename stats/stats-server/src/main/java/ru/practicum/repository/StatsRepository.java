package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.StatsHit;

public interface StatsRepository extends JpaRepository<StatsHit, Long> {
}
