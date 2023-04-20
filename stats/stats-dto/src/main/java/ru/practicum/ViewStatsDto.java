package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ViewStatsDto {
    private String app;
    private String uri;
    private Integer hits;
}
