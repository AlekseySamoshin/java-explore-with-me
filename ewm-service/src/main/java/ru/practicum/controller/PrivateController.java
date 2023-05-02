package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class PrivateController {
    EventService eventService;

    @Autowired
    public PrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос: Получение событий, добавленных пользователем id=" + userId);
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addNewEventByUser(@PathVariable Long userId,
                                          @RequestBody NewEventDto newEvent) {

        log.info("Запрос: Добавление нового события пользователем id=" + userId);
        return eventService.addNewEventByUser(userId, newEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventOfUserByIds(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("Запрос: Получение полной информации о событии id="+ eventId + ", пользователем id=" + userId);
        return eventService.getEventOfUserByIds(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventOfUserByIds(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody UpdateEventUserRequest request) {
        log.info("Запрос: Изменение события id=" + eventId + ", пользователем id=" + userId);
        return eventService.updateEventOfUserByIds(userId, eventId, request);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequest(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("Запрос: Получение информации о запросах на участие в событии пользователя id=" + userId);
        return eventService.getParticipationRequestsDto(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequest(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Запрос: Изменение статуса заявок на участие в событии пользователя id=" + userId);
        return eventService.updateParticipationRequest(userId, eventId, request);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable Long userId) {
        log.info("Запрос: Получение информации о заявках пользователя id=" + userId + " на участие в чужих событиях");
        return eventService.getParticipationRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId, @RequestParam Long evenId) {
//    нельзя добавить повторный запрос (Ожидается код ошибки 409)
//    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
//    нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
//    если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
//    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        log.info("Запрос: Заявка пользователем id=" + userId + " запроса на участие в событии id=" + evenId);
        return eventService.addParticipationRequest(userId, evenId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
        log.info("Запрос: Отмена пользователем id=" userId + " запроса на участие  id=" + requestId);
        return eventService.cancelParticipationRequest(userId, requestId);
    }
}
