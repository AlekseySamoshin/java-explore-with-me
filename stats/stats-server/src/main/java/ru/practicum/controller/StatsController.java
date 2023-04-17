package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDto> getStats(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "start") LocalDateTime start,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "end") LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики");
        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    HitRequestDto hitStats(@RequestBody @Valid HitRequestDto hitRequestDto) {
        log.info("Запрос на сохранение факта обращения к эндпоинту");
        return statsService.saveHit(hitRequestDto);
    }
}
