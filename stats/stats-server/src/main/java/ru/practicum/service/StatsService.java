package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.StatsHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

//    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
//        return null;
//    }
//
//    public List<ViewStatsDto> getStats(LocalDateTime start,
//                                       LocalDateTime end,
//                                       List<String> uris,
//                                       Boolean unique) {
//        return null;
//    }


    //    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto statsHit) {
        statRepository.save(statsHit);
        return statsHit;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {

        if (unique) {
            return statsRepository.findUniqueStats(start, end, uris);
        }

        return statsRepository.findStats(start, end, uris);
    }


    private EndpointHitDto convertStatsHitToDto(StatsHit statsHit) {
        return EndpointHitDto.builder()
                .id(statsHit.getId())
                .ip(statsHit.getIp())
                .app(statsHit.getApp())
                .uri(statsHit.getUri())
                .timestamp(statsHit.getTimestamp())
                .build();
    }

    private ViewStatsDto convertViewStatsToDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .hits(viewStats.getHits())
                .uri(viewStats.getUri())
                .build();
    }
}
