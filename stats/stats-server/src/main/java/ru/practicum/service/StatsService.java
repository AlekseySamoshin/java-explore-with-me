package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {

        if (endpointHitDto.getApp() == null || endpointHitDto.getApp().isEmpty()) {
            throw new WrongDataException("Передано пустое поле app");
        }

        if (endpointHitDto.getUri() == null || endpointHitDto.getUri().isEmpty()) {
            throw new WrongDataException("Передано пустое поле uri");
        }

        return convertStatsHitToDto(statsRepository.save(convertHitDtoToHit(endpointHitDto)));
//        statsRepository.save(endpointHitDto);
//        return endpointHitDto;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

//        LocalDateTime start = LocalDateTime.parse(URLDecoder.decode(startString, StandardCharsets.UTF_8),
//                DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss")));
//        LocalDateTime end = LocalDateTime.parse(URLDecoder.decode(endString, StandardCharsets.UTF_8),
//                DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss")));


        if (unique) {
            return statsRepository.findUniqueStats(start, end, uris).stream()
                    .map(this::convertViewStatsToDto)
                    .collect(Collectors.toList());
        }

        return statsRepository.findStats(start, end, uris).stream()
                .map(this::convertViewStatsToDto)
                .collect(Collectors.toList());
    }


    private EndpointHitDto convertStatsHitToDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .ip(endpointHit.getIp())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    private EndpointHit convertHitDtoToHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .id(endpointHitDto.getId())
                .ip(endpointHitDto.getIp())
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .timestamp(endpointHitDto.getTimestamp())
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
