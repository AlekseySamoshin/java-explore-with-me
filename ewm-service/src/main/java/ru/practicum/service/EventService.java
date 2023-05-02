package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.*;
import ru.practicum.dtoMapper.CategoryDtoMapper;
import ru.practicum.dtoMapper.EventDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final CategoryDtoMapper categoryDtoMapper;
    private final EventDtoMapper eventDtoMapper;
    private final EventRepository eventRepository;
    private final RequestDtoMapper requestDtoMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime rangeStartDateTime = LocalDateTime.parse(rangeStart);
        LocalDateTime rangeEndDateTime = LocalDateTime.parse(rangeEnd);
        return eventRepository.findAllEvents(users, states, categories, rangeStartDateTime, rangeEndDateTime, PageRequest.of(from / size, size)).stream()
                .map(eventDtoMapper::mapEventToFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие id=" + eventId + " не найдено"));

        if (LocalDateTime.now().isAfter(event.getEventDate().minus(1, ChronoUnit.HOURS))) {
            throw new ConflictException("До начала события меньше часа, изменение невозможно");
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Событие не в состоянии ожидания публикации");
        }
        if ((!updateRequest.getStateAction().equals(StateAction.REJECT_EVENT.toString())
                && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }
        updateEventWithAdminRequest(event, updateRequest);
        event = eventRepository.save(event);
        return eventDtoMapper.mapEventToFullDto(event);
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(categoryDtoMapper.mapNewDtoToCategory(newCategoryDto));
        log.info("Категория сохранена с id=" + category.getId());
        return categoryDtoMapper.mapCategoryToDto(category);
    }

    public void deleteCategoryById(Long catId) {
        if (categoryIsEmpty(catId)) {
            categoryRepository.deleteById(catId);
            log.info("Категория id=" + catId + " удалена");
        } else {
            throw new ConflictException("Категория id=" + catId + " не пуста");
        }
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория id=" + catId + " не найдена")
        );
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new WrongDataException("Передано пустое поле name");
        }
        category.setName(categoryDto.getName());
        return categoryDtoMapper.mapCategoryToDto(category);
    }

    private boolean categoryIsEmpty(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория id=" + catId + " не найдена")
        );
        List<Event> eventsOfCategory = eventRepository.findAllByCategoryId(catId);
        return eventsOfCategory.isEmpty();
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryDtoMapper::mapCategoryToDto)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        return eventRepository.findAllByUserId(user, PageRequest.of(from / size, size)).stream()
                .map(eventDtoMapper::mapEventToShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto addNewEventByUser(Long userId, NewEventDto newEvent) {
        User user = getUserById(userId);
        Category category = categoryRepository.findById(newEvent.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория id=" + newEvent.getCategory() + " не найдена")
        );
        Event event = eventRepository.save(eventDtoMapper.mapNewEventDtoToEvent(newEvent, category));
        log.info("Событие сохранено с id=" + event.getId());
        return eventDtoMapper.mapEventToFullDto(event);
    }

    public EventFullDto getEventOfUserByIds(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        return eventDtoMapper.mapEventToFullDto(event);
    }

    public EventFullDto updateEventOfUserByIds(Long userId, Long eventId, UpdateEventUserRequest request) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        event = updateEventWithUserRequest(event, request);
        return eventDtoMapper.mapEventToFullDto(event);
    }

    public List<ParticipationRequestDto> getParticipationRequestsDto(Long userId, Long eventId) {
        List<ParticipationRequest> requests = getParticipationRequests(userId, eventId);
        return requests.stream()
                .map(requestDtoMapper::mapRequestToDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateParticipationRequest(Long userId,
                                                                     Long eventId,
                                                                     EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequest> requests = getParticipationRequests(userId, eventId);
        for (ParticipationRequest request : requests) {
            if (updateRequest.getRequestIds().contains(request.getId())) {
                request.setStatus(updateRequest.getStatus());
            }
        }
        for (ParticipationRequest request : requests) {
            switch (request.getStatus().toUpperCase()) {
                case "CONFIRMED":
                    result.getConfirmedRequests().add(request);
                case "REJECTED":
                    result.getRejectedRequests().add(request);
                default:
                    throw new WrongDataException("Неверный статус запроса подтверждения/отклонения");
            }
        }
        return result;
    }

    private List<ParticipationRequest> getParticipationRequests(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        return requestRepository.findByUserId(userId);
    }

    private Event updateEventWithUserRequest(Event event, UpdateEventUserRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверный аргумент для публикации/отклонения события");
            }
        }
        return event;
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие id=" + eventId + " не найдено"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь id=" + userId + " не найден"));
    }

    private Event updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверный аргумент для публикации/отклонения события");
            }
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        return event;
    }
}
