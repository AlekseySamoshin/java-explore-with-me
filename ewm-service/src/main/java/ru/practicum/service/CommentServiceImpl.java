package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentDto;
import ru.practicum.dtoMapper.CommentDtoMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;


    @Override
    public CommentDto addNewComment(Long userId, Long eventId, CommentDto newCommentDto) {
        if (newCommentDto.getText() == null || newCommentDto.getText().isBlank()) {
            throw new WrongDataException("Не удалось добавить комментарий", "текст комментария отсутствует");
        }
        if (newCommentDto.getText().length() > 2048) {
            throw new WrongDataException("Не удалось добавить комментарий", "слишком длинный комментарий");
        }
        Comment comment = commentRepository.save(getCommentFromDto(userId, eventId, newCommentDto));
        log.info("комментарий сохранен с id=" + comment.getId());
        return commentDtoMapper.mapCommentToDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsOfEvent(Long eventId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size));
        log.info("найдено комментариев: " + comments.size());
        return comments.stream()
                .map(commentDtoMapper::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsOfUser(Long userId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAllByUserId(userId, PageRequest.of(from / size, size));
        log.info("найдено комментариев: " + comments.size());
        return comments.stream()
                .map(commentDtoMapper::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto updateCommentDto) {
        Comment newComment = getCommentFromDto(userId, commentId, updateCommentDto);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Не удалось изменить комментарий", "комментарий id=" + commentId + "не найден")
        );
        comment.setText(newComment.getText());
        comment.setUpdated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("комментарий id=" + commentId + " обновлен");
        return commentDtoMapper.mapCommentToDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не удалось удалить комментарий", "пользователь id=" + userId + " не найден")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("не удалось удалить комментарий", "комментарй id=" + commentId + " не найден")
        );
        if (userId.equals(comment.getAuthor().getId())) {
            commentRepository.deleteById(commentId);
            log.info("комментарий id=" + commentId + " удален");
        } else {
            throw new WrongDataException("невозможно удалить комментарий id=" + commentId,
                    "пользователь id=" + userId + " не является автором комментария");
        }
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("не удалось удалить комментарий", "комментарй id=" + commentId + " не найден")
        );
        commentRepository.deleteById(commentId);
        log.info("комментарий id=" + commentId + " удален");
    }

    @Override
    public List<CommentDto> findCommentsByText(Long eventId, String text, Integer from, Integer size) {
        return commentRepository.findByEventIdAndText(eventId, text.toLowerCase(), PageRequest.of(from / size, size));
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("не удалось получить комментарий", "комментарий id=" + commentId + " не найден")
        );
        return commentDtoMapper.mapCommentToDto(comment);
    }

    private Comment getCommentFromDto(Long userId, Long eventId, CommentDto commentDto) {
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
