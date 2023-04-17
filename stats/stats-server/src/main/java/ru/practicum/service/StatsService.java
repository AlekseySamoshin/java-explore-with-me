package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public HitRequestDto saveHit(HitRequestDto hitRequestDto) {
        return null;
    }

    public List<StatsDto> getStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris,
                                   Boolean unique) {
        return null;
    }
}
