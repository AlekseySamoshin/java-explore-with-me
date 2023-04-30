package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    //    ВЫГЛЯДИТ КАК UpdateCompilationRequest
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
