package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NewCategoryDto {
    private String name;
}
