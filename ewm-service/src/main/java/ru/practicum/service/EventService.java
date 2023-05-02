package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
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
import java.util.ArrayList;
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
    private final StatsClient statsClient;

    private final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

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

    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId) {
        User user = getUserById(userId);
        return requestRepository.findByUserId(userId).stream()
                .map(requestDtoMapper::mapRequestToDto)
                .collect(Collectors.toList());
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
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(dateTimeFormat)));
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
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(dateTimeFormat)));
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

    public ParticipationRequestDto addParticipationRequest(Long userId, Long evenId) {
//    нельзя добавить повторный запрос (Ожидается код ошибки 409)
//    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
//    нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
//    если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
//    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        User user = getUserById(userId);
        Event event = getEventById(evenId);
        List<ParticipationRequest> requests = getParticipationRequests(userId, evenId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Невозможно добавить заявку на участие в своём событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Невозможно оставить заявку: событие не опубликовано");
        }
        if (event.getParticipantLimit() >= requests.size()) {
            throw new ConflictException("Невозможно оставить заявку: количество заявок на участие максимально");
        }
        for (ParticipationRequest request : requests) {
            if (request.getRequester().equals(userId)) {
                throw new ConflictException("Оставить заявку повторно невозможно");
            }
        }

        ParticipationRequest newRequest = ParticipationRequest.builder()
                .requester(userId)
                .created(LocalDateTime.now())
                .status("PENDING")
                .event(event)
                .build();
        if (event.getRequestModeration().equals(false)) {
            newRequest.setStatus("ACCEPTED");
        }
        return requestDtoMapper.mapRequestToDto(requestRepository.save(newRequest));
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        User user = getUserById(userId);
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос id=" + requestId + " не найден")
        );
        if (!request.getRequester().equals(userId)) {
            throw new ConflictException("Заявка id=" + requestId + " оставлена не пользователем id=" + userId);
        }
        request.setStatus("CANCELLED");
        log.info("Отмена заявки на участие id=" + requestId);
        return requestDtoMapper.mapRequestToDto(requestRepository.save(request));
    }

    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Подбока id=" + catId + " не найдена"));
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
//    + это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
//    + текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
//    + если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
//   -+ информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
//    + информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        List<Event> events;
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (rangeStart == null) {
            startDate = LocalDateTime.now();
        } else {
            startDate = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(dateTimeFormat));
        }
        if (rangeEnd == null) {
            events = eventRepository.findEventsByText(text.toLowerCase(), PageRequest.of(from / size, size));
        } else {
            endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(dateTimeFormat));
            events = eventRepository.findAllByTextAndDateRange(text.toLowerCase(),
                                                                startDate,
                                                                endDate,
                                                                PageRequest.of(from / size, size));
        }

        events = events.stream()
                .filter((event) -> event.getState().equals(EventState.PUBLISHED))
                .collect(Collectors.toList());


        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("evm-service")
                .uri("/events/")
                .ip(ip)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .build();
        statsClient.saveHit(endpointHitDto);

        return createShortEventDtos(events);
    }

    private List<EventShortDto> createShortEventDtos(List<Event> events) {
        List<Long> eventIds = new ArrayList<>();
        for(Event event : events) {
            eventIds.add(event.getId());
        }
        List<ParticipationRequest> requests = requestRepository.finByEventIds(eventIds);
        List<EventShortDto> dtos = events.stream().map(eventDtoMapper::mapEventToShortDto).collect(Collectors.toList());
        for(EventShortDto dto : dtos) {
            for(ParticipationRequest request : requests) {
                if(request.getEvent().getId().equals(dto.getId()) && request.getStatus().equals("ACCEPTED")) {
                    dto.setConfirmedRequests(dto.getConfirmedRequests() + 1);
                }
            }
        }
        return dtos;
    }
}
