package ru.practicum.service;

import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEvent);

    EventFullDto getEventOfUserByIds(Long userId, Long eventId);

    EventFullDto updateEventOfUserByIds(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getParticipationRequestsDto(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateParticipationRequest(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId);

    EventFullDto getEventDtoById(Long eventId, HttpServletRequest request);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    CategoryDto getCategoryById(Long catId);

    List<EventShortDto> getEventsWithFilters(String text,
                                             List<Integer> categories,
                                             Boolean paid,
                                             String rangeStart,
                                             String rangeEnd,
                                             Boolean onlyAvailable,
                                             String sort,
                                             Integer from,
                                             Integer size,
                                             HttpServletRequest request);
}
