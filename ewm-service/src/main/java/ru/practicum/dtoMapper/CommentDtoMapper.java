package ru.practicum.dtoMapper;

import org.springframework.stereotype.Component;
import ru.practicum.Constants;
import ru.practicum.dto.CommentDto;
import ru.practicum.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommentDtoMapper {
    EventDtoMapper eventDtoMapper = new EventDtoMapper();
    UserDtoMapper userDtoMapper = new UserDtoMapper();

    public CommentDto mapCommentToDto(Comment comment) {
        String updated = null;
        if (comment.getUpdated() != null) {
            updated = comment.getUpdated().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        }
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(eventDtoMapper.mapEventToShortDto(comment.getEvent()))
                .author(userDtoMapper.mapUserToShortDto(comment.getAuthor()))
                .created(comment.getCreated().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .updated(updated)
                .build();
    }
}
