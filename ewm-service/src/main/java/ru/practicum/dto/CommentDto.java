package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private UserShortDto author;
    private EventShortDto event;
    private String text;
    private String created;
    private String updated;
}
