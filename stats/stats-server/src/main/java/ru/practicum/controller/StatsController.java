package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsDto;
import ru.practicum.StatsServer;

@RestController
public class StatsController {
    private final StatsServer statsServer;
    private final StatsDto statsDto;
    private final HitRequestDto hitRequestDto;

    @Autowired
    public StatsController (StatsServer statsServer, StatsDto statsDto, HitRequestDto hitRequestDto) {
        this.statsServer = statsServer;
        this.statsDto = statsDto;
        this.hitRequestDto = hitRequestDto;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    StatsDto viewStats() {
        return null;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    HitRequestDto hitStats(){
        return null;
    }

}
