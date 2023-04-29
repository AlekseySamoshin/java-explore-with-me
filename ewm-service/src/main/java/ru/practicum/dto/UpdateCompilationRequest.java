package ru.practicum.dto;

import java.util.List;

public class UpdateCompilationRequest {
//    ВЫГЛЯДИТ КАК NewCompilationDto
    List<Long> events;
    Boolean pinned;
    String title;
}
