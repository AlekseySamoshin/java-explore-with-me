package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HitRequestDto {
    private String start;
    private String end;
    private List<String> uris;
    private Boolean unique;
}
