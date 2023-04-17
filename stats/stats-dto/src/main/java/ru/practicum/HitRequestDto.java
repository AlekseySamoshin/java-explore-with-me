package ru.practicum;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class HitRequestDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
