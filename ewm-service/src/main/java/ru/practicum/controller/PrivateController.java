package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@Slf4j
@AllArgsConstructor
public class PrivateController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping("/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос: Получение событий, добавленных пользователем id=" + userId);
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEventByUser(@PathVariable Long userId,
                                          @RequestBody NewEventDto newEvent) {
        log.info("Запрос: Добавление нового события пользователем id=" + userId);
        return eventService.addEvent(userId, newEvent);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventOfUserByIds(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("Запрос: Получение полной информации о событии id=" + eventId + ", пользователем id=" + userId);
        return eventService.getEventOfUserByIds(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventOfUserByIds(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody UpdateEventUserRequest request) {
        log.info("Запрос: Изменение события id=" + eventId + ", пользователем id=" + userId);
        return eventService.updateEventOfUserByIds(userId, eventId, request);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequest(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("Запрос: Получение информации о запросах на участие в событии пользователя id=" + userId);
        return eventService.getParticipationRequestsDto(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequest(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Запрос: Изменение статуса заявок на участие в событии пользователя id=" + userId);
        return eventService.updateParticipationRequest(userId, eventId, request);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable Long userId) {
        log.info("Запрос: Получение информации о заявках пользователя id=" + userId + " на участие в чужих событиях");
        return eventService.getParticipationRequestsByUserId(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId,
                                                           @RequestParam Long eventId) {
        log.info("Запрос: Заявка пользователем id=" + userId + " запроса на участие в событии id=" + eventId);
        return eventService.addParticipationRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        log.info("Запрос: Отмена пользователем id=" + userId + " запроса на участие  id=" + requestId);
        return eventService.cancelParticipationRequest(userId, requestId);
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addNewComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody CommentDto newCommentDto) {
        log.info("Запрос: создание комментария пользователем id=" + userId + " к событию id=" + eventId);
        return commentService.addNewComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/comments") // получение всех комментов юзера
    public List<CommentDto> getCommentsByUser(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос: получение комментариев пользователя id=" + userId);
        return commentService.getCommentsOfUser(userId, from, size);
    }

    @PatchMapping("comments/{commentId}") // правки коммента автором
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentDto updateCommentDto) {
        log.info("Запрос: изменение комментария id=" + commentId + " пользователем id=" + userId);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("comments/{commentId}") // удаление коммента автором
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Запрос: удаление комментария id=" + commentId);
        commentService.deleteComment(userId, commentId);
        log.info("комментарий id=" + commentId + " удален");
    }
}
