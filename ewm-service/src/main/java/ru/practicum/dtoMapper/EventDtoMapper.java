package ru.practicum.dtoMapper;

import org.springframework.stereotype.Component;
import ru.practicum.Constants;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventDtoMapper {
    private final String dateTimeFormat = Constants.DATE_TIME_FORMAT;
    private final CategoryDtoMapper categoryDtoMapper = new CategoryDtoMapper();
    private final UserDtoMapper userDtoMapper = new UserDtoMapper();

    public EventFullDto mapEventToFullDto(Event event) {
        if (event.getState() == null) {
            event.setState(EventState.PENDING);
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDtoMapper.mapCategoryToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .initiator(userDtoMapper.mapUserToShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(checkPublishedOn(event))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto mapEventToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(categoryDtoMapper.mapCategoryToDto(event.getCategory()))
                .initiator(userDtoMapper.mapUserToShortDto(event.getInitiator()))
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

    private String checkPublishedOn(Event event) {
        if (event.getPublishedOn() == null) {
            return null;
        }
        return event.getPublishedOn().format(DateTimeFormatter.ofPattern(dateTimeFormat));
    }
}
