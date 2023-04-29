package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users")
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
//        Получение событий, добавленных текущим пользователем
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addNewEventByUser(@PathVariable Long userId,
                                          @RequestBody NewEventDto newEvent) {

//        Добавление нового события
        return eventService.addNewEventByUser(userId, newEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventOfUserByIds(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
//    Получение полной информации о событии добавленном текущим пользователем
        return eventService.getEventOfUserByIds(userId, eventId);
    }


    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventOfUserByIds(@PathVariable Long userId,
                                               @PathVariable Long eventId) {
//    Изменение события добавленного текущим пользователем
        return eventService.updateEventOfUserByIds(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ParticipationRequestDto getParticipationRequest(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
//    Получение информации о запросах на участие в событии текущего пользователя
        return eventService.getParticipationRequest(userIdm eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateParticipationRequest(@PathVariable Long userId,
                                                                     @PathVariable Long eventId,
                                                                     @RequestBody EventRequestStatusUpdateRequest request) {
//    Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
        return eventService.updateParticipationRequest(userId, eventId, request);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsOfUser(@PathVariable Long userId) {
//    Получение информации о заявках текущего пользователя на участие в чужих событиях
        return eventService.getParticipationRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId, @RequestParam Long evenId) {
//    нельзя добавить повторный запрос (Ожидается код ошибки 409)
//    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
//    нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
//    если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
//    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        return eventService.addParticipationRequest(userId, evenId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId,
                                                              @PathVariable Long requestId) {
//    Отмена своего запроса на участие в событии
        return eventService.cancelParticipationRequest(userId, requestId);
    }
}
