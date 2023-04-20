package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewStats {
    private String app;
    private String uri;
    private Integer hits;
}
