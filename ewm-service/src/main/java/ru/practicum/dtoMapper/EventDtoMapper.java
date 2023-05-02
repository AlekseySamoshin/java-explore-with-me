package ru.practicum.dtoMapper;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDtoMapper {
    private final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    CategoryDtoMapper categoryDtoMapper = new CategoryDtoMapper();

    public EventFullDto mapEventToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDtoMapper.mapCategoryToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto mapEventToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .views(event.getViews())
                .category(categoryDtoMapper.mapCategoryToDto(event.getCategory()))
                .initiator(event.getInitiator())
                .id(event.getId())
                .paid(event.getPaid())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .build();
    }

    public Event mapNewEventDtoToEvent(NewEventDto newEvent, Category category) {
        return Event.builder()
                .annotation(newEvent.getAnnotation())
                .category(category)
                .description(newEvent.getDescription())
                .eventDate(LocalDateTime.parse(newEvent.getEventDate(), DateTimeFormatter.ofPattern(dateTimeFormat)))
                .location(newEvent.getLocation())
                .paid(newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit())
                .requestModeration(newEvent.getRequestModeration())
                .title(newEvent.getTitle())
                .build();
    }
}
