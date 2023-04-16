package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsServer;

@RestController
public class StatsController {
    private final StatsServer statsServer;
    private final StatsRequestDto statsRequestDto;
    private final HitRequestDto hitRequestDto;

    @Autowired
    public StatsController (StatsServer statsServer, StatsRequestDto statsRequestDto, HitRequestDto hitRequestDto) {
        this.statsServer = statsServer;
        this.statsRequestDto = statsRequestDto;
        this.hitRequestDto = hitRequestDto;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    StatsRequestDto viewStats() {
        return null;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    HitRequestDto hitStats(){
        return null;
    }

}
