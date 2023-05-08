package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.Constants;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.*;
import ru.practicum.dtoMapper.CategoryDtoMapper;
import ru.practicum.dtoMapper.EventDtoMapper;
import ru.practicum.dtoMapper.RequestDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final LocationRepository locationRepository;

    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime rangeStartDateTime = null;
        LocalDateTime rangeEndDateTime = null;
        if (rangeStart != null) {
            rangeStartDateTime = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        }
        if (rangeEnd != null) {
            rangeEndDateTime = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        }
        List<EventState> eventStateList;
        if (states != null) {
            eventStateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        } else {
            eventStateList = Arrays.stream(EventState.values()).collect(Collectors.toList());
        }
        List<EventFullDto> dtos = eventRepository.findAllEventsWithDates(users, eventStateList, categories, rangeStartDateTime, rangeEndDateTime, PageRequest.of(from / size, size)).stream()
                .map(eventDtoMapper::mapEventToFullDto)
                .collect(Collectors.toList());
        dtos = getViewCounters(dtos);
        return dtos;
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        String errorMessage = "не удалось обновить событие id=" + eventId;
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(errorMessage, "Событие id=" + eventId + " не найдено" ));

        if (LocalDateTime.now().isAfter(event.getEventDate().minus(2, ChronoUnit.HOURS))) {
            throw new ConflictException(errorMessage, "До начала события меньше часа, изменение невозможно" );
        }
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException(errorMessage, "Событие не в состоянии ожидания публикации" );
        }
        if ((!StateAction.REJECT_EVENT.toString().equals(updateRequest.getStateAction())
                && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException(errorMessage, "Отклонить опубликованное событие невозможно" );
        }
        updateEventWithAdminRequest(event, updateRequest);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException(errorMessage, "событие уже прошло");
        }
        event = eventRepository.save(event);
        EventFullDto eventFullDto = eventDtoMapper.mapEventToFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.findByName(newCategoryDto.getName()).size() > 0) {
            throw new ConflictException("не удалось создать категорию", "название (" + newCategoryDto.getName() +") уже занято");
        }
        Category category = categoryRepository.save(categoryDtoMapper.mapNewDtoToCategory(newCategoryDto));
        log.info("Категория сохранена с id=" + category.getId());
        return categoryDtoMapper.mapCategoryToDto(category);
    }

    public void deleteCategoryById(Long catId) {
        if (categoryIsEmpty(catId)) {
            categoryRepository.deleteById(catId);
            log.info("Категория id=" + catId + " удалена" );
        } else {
            throw new ConflictException("не удалось удалить категорию", "Категория id=" + catId + " не пуста" );
        }
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("не удалось обновить категорию", "Категория id=" + catId + " не найдена" )
        );
        if (categoryDto.getName() == null || categoryDto.getName().isBlank()) {
            throw new WrongDataException("не удалось обновить категорию", "Передано пустое поле name" );
        }
        if (categoryRepository.findByName(categoryDto.getName()).size() > 0) {
            throw new ConflictException("не удалось обновить категорию id=" + category.getId(), "название (" + categoryDto.getName() +") уже занято");
        }
        category.setName(categoryDto.getName());
        return categoryDtoMapper.mapCategoryToDto(category);
    }

    private boolean categoryIsEmpty(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("не удалось удалить категорию", "Категория id=" + catId + " не найдена" )
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
        User user = userService.getUserById(userId);
        return eventRepository.findAllByUserId(user, PageRequest.of(from / size, size)).stream()
                .map(eventDtoMapper::mapEventToShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(Long userId, NewEventDto newEvent) {
        User user = userService.getUserById(userId);
        Category category = categoryRepository.findById(newEvent.getCategory()).orElseThrow(
                () -> new NotFoundException("не удалось добавить событие", "Категория id=" + newEvent.getCategory() + " не найдена" )
        );
        Event event = eventDtoMapper.mapNewEventDtoToEvent(newEvent, category);
        event.setLocation(locationRepository.save(event.getLocation()));
        log.info("Сохранена локация id = " + event.getLocation().getId() + " для нового события" );
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        if (LocalDateTime.now().isAfter(event.getEventDate().minus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("не удалось добавить событие", "До начала события меньше часа, изменение невозможно" );
        }
        event = eventRepository.save(event);
        log.info("Событие сохранено с id=" + event.getId());
        EventFullDto eventFullDto = eventDtoMapper.mapEventToFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    public EventFullDto getEventOfUserByIds(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("неверный запрос на поиск события", "Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        EventFullDto eventFullDto = eventDtoMapper.mapEventToFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    public EventFullDto updateEventOfUserByIds(Long userId, Long eventId, UpdateEventUserRequest request) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("не удалось обновить событие", "Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        event = updateEventWithUserRequest(event, request);
        EventFullDto eventFullDto = eventDtoMapper.mapEventToFullDto(event);
        return getViewsCounter(eventFullDto);
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
        Event event = getEventById(eventId);
        List<ParticipationRequest> requests = getParticipationRequestsByEventId(eventId);
        if (participationLimitIsFull(event, requests)) {
            throw new ConflictException("обновление события id=" + eventId + " невозможно", "достигнут лимит заявок");
        }
        for (ParticipationRequest request : requests) {
            if (updateRequest.getRequestIds().contains(request.getId())) {
                request.setStatus(updateRequest.getStatus());
            }
        }
        for (ParticipationRequest request : requests) {
            if (request.getStatus().equals("CONFIRMED" ) || request.getStatus().equals("REJECTED" ) || request.getStatus().equals("PENDING")) {
                result.addRequest(request);
                requestRepository.save(request);
            } else {
                throw new WrongDataException("не удалось обновить запрос", "Неверный статус запроса подтверждения/отклонения" );
            }
        }
        return result;
    }

    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return requestRepository.findByUserId(userId).stream()
                .map(requestDtoMapper::mapRequestToDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventDtoById(Long eventId) {
        Event event = getEventById(eventId);
        EventFullDto eventFullDto = eventDtoMapper.mapEventToFullDto(event);
        return getViewsCounter(eventFullDto);
    }

    private boolean participationLimitIsFull(Event event, List<ParticipationRequest> requests) {
        Integer confirmedRequestsCounter = 0;
        for (ParticipationRequest request : requests) {
            if (request.getStatus().equals("ACCEPTED") || request.getStatus().equals("CONFIRMED")) {
                confirmedRequestsCounter += 1;
            }
        }
        if (event.getParticipantLimit() <= confirmedRequestsCounter) {
            throw new ConflictException("не удалось добавить запрос на участие", "количество заявок на участие максимально" );
        }
        return false;

    }

    private List<ParticipationRequest> getParticipationRequests(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new WrongDataException("не удалось найти запросы на участие", "Пользователь id=" + userId + " не инициатор события id=" + eventId);
        }
        return requestRepository.findByUserId(userId);
    }

    private List<ParticipationRequest> getParticipationRequestsByEventId(Long eventId) {
        Event event = getEventById(eventId);
        return requestRepository.findByEventId(eventId);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("не удалось получить событие", "Событие id=" + eventId + " не найдено" ));
    }

    private Event updateEventWithUserRequest(Event event, UpdateEventUserRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("не удалось обновить событие", "Категория id=" + updateRequest.getCategory() + "не найдена" ));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
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
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                default:
                    throw new WrongDataException("не удалось обновить событие", "Неверный аргумент для публикации/отклонения события" );
            }
        }
        if (LocalDateTime.now().isAfter(event.getEventDate().minus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("не удалось обновить событие id=" + event.getId(), "до начала события осталось меньше 2 часов");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("не удалось обновить событие id=" + event.getId(), "нельзя изменить опубликованное событие");
        }
        return event;
    }

    private Event updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("не удалось обновить событие", "Категория id=" + updateRequest.getCategory() + "не найдена" ));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)));
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
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("не удалось обновить событие", "Неверный аргумент для публикации/отклонения события" );
            }
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        return event;
    }

    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("не удалось добавить запрос на участие", "Невозможно добавить заявку на участие в своём событии" );
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("не удалось добавить запрос на участие", "событие не опубликовано" );
        }
        List<ParticipationRequest> requests = getParticipationRequestsByEventId(event.getId());
        if (participationLimitIsFull(event, requests)) {
            throw new ConflictException("не удалось добавить запрос на участие", "достигнут лимит заявок" );
        }
        for (ParticipationRequest request : requests) {
            if (request.getRequester().equals(userId)) {
                throw new ConflictException("не удалось добавить запрос на участие", "Оставить заявку повторно невозможно" );
            }
        }
        ParticipationRequest newRequest = ParticipationRequest.builder()
                .requester(userId)
                .created(LocalDateTime.now())
                .status("PENDING" )
                .event(event)
                .build();
        if (event.getRequestModeration().equals(false)) {
            newRequest.setStatus("ACCEPTED" );
        }
        return requestDtoMapper.mapRequestToDto(requestRepository.save(newRequest));
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        User user = userService.getUserById(userId);
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("не удалось отменить запрос на участие", "Запрос id=" + requestId + " не найден" )
        );
        if (!request.getRequester().equals(userId)) {
            throw new ConflictException("не удалось отменить заявку", "Заявка id=" + requestId + " оставлена не пользователем id=" + userId);
        }
        request.setStatus("CANCELLED" );
        log.info("Отмена заявки на участие id=" + requestId);
        return requestDtoMapper.mapRequestToDto(requestRepository.save(request));
    }

    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("не удалось получить категорию", "Категория id=" + catId + " не найдена" ));
        return categoryDtoMapper.mapCategoryToDto(category);
    }

    public List<EventShortDto> getEventsWithFilters(String text,
                                                    List<Integer> categories,
                                                    Boolean paid,
                                                    String rangeStart,
                                                    String rangeEnd,
                                                    Boolean onlyAvailable,
                                                    String sort,
                                                    Integer from,
                                                    Integer size,
                                                    String ip) {
        List<Event> events;
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (rangeStart == null) {
            startDate = LocalDateTime.now();
        } else {
            startDate = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        }
        if (rangeEnd == null) {
            events = eventRepository.findEventsByText(text.toLowerCase(), PageRequest.of(from / size, size));
        } else {
            endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
            events = eventRepository.findAllByTextAndDateRange(text.toLowerCase(),
                    startDate,
                    endDate,
                    PageRequest.of(from / size, size));
        }

        events = events.stream()
                .filter((event) -> event.getState().equals(EventState.PUBLISHED))
                .collect(Collectors.toList());

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("evm-service" )
                .uri("/events/" )
                .ip(ip)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .build();
        statsClient.saveHit(endpointHitDto);
        return createShortEventDtos(events);
    }

    private List<EventShortDto> createShortEventDtos(List<Event> events) {
        HashMap<Long, Integer> eventIdsWithViewsCounter = new HashMap<>();
        for (Event event : events) {
            eventIdsWithViewsCounter.put(event.getId(), getViewsCounter(eventDtoMapper.mapEventToFullDto(event)).getViews());
        }
        List<ParticipationRequest> requests = requestRepository.findByEventIds(new ArrayList<>(eventIdsWithViewsCounter.keySet()));
        List<EventShortDto> dtos = events.stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList());
        for (EventShortDto dto : dtos) {
            for (ParticipationRequest request : requests) {
                if (request.getEvent().getId().equals(dto.getId()) && request.getStatus().equals("ACCEPTED" )) {
                    dto.setConfirmedRequests(dto.getConfirmedRequests() + 1);
                }
            }
            dto.setViews(eventIdsWithViewsCounter.get(dto.getId()));
        }
        return dtos;
    }

    private EventFullDto getViewsCounter(EventFullDto eventFullDto) {
        Integer views = statsClient.getStats(eventFullDto.getCreatedOn(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)),
                List.of("/events/" + eventFullDto.getId()), true).size();
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private List<EventFullDto> getViewCounters(List<EventFullDto> dtos) {
        for (EventFullDto dto : dtos) {
            getViewsCounter(dto);
        }
        return dtos;
    }
}
