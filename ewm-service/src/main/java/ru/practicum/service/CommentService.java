package ru.practicum.service;

import ru.practicum.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addNewComment(Long userId, Long eventId, CommentDto newCommentDto);

    List<CommentDto> getCommentsOfEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsOfUser(Long userId, Integer from, Integer size);

    CommentDto updateComment(Long userId, Long commentId, CommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> findCommentsByText(Long eventId, String text, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}
