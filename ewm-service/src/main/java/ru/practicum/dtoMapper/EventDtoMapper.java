package ru.practicum.dtoMapper;

import ru.practicum.dto.EventFullDto;
import ru.practicum.model.Event;

import java.time.format.DateTimeFormatter;

public class EventDtoMapper {
    CategoryDtoMapper categoryDtoMapper = new CategoryDtoMapper();
    public EventFullDto mapEventToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDtoMapper.mapCategoryToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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
}
