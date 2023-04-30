package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private String eventDate;
    private String id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
