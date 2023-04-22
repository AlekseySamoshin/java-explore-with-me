package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.DtoMapper;
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

    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {

        if (endpointHitDto.getApp() == null || endpointHitDto.getApp().isEmpty()) {
            throw new WrongDataException("Передано пустое поле app");
        }

        if (endpointHitDto.getUri() == null || endpointHitDto.getUri().isEmpty()) {
            throw new WrongDataException("Передано пустое поле uri");
        }

        return DtoMapper.mapEnpointHitDto(statsRepository.save(DtoMapper.mapDtoToEndpointHit(endpointHitDto)));
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (unique) {
            if (uris != null) {
                return statsRepository.findUniqueStats(start, end, uris).stream()
                        .map(DtoMapper::mapViewStatsToDto)
                        .collect(Collectors.toList());
            }
            return statsRepository.findUniqueStats(start, end).stream()
                    .map(DtoMapper::mapViewStatsToDto)
                    .collect(Collectors.toList());
        }

        if (uris != null) {
            return statsRepository.findStats(start, end, uris).stream()
                    .map(DtoMapper::mapViewStatsToDto)
                    .collect(Collectors.toList());
        }
        return statsRepository.findStats(start, end).stream()
                .map(DtoMapper::mapViewStatsToDto)
                .collect(Collectors.toList());
    }
}
