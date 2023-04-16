package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class StatsDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
