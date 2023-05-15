package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentServiceInteface {
    CommentDto addNewComment(Long userId, Long eventId, CommentDto newCommentDto);

    List<CommentDto> getCommentsOfEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsOfUser(Long userId, Integer from, Integer size);

    CommentDto updateComment(Long userId, Long commentId, CommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> findCommentsByText(Long eventId, String text, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    default Comment getCommentFromDto(Long userId, Long eventId, CommentDto commentDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Не удалось добавить комментарий", "событие id=" + eventId + "не найдено")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Не удалось добавить комментарий", "пользователь id=" + eventId + "не найден")
        );
        return Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
    }
}
