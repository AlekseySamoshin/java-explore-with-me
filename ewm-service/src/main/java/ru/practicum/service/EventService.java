package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dtoMapper.CategoryDtoMapper;
import ru.practicum.dtoMapper.EventDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.StateAction;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

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
    private final CategoryRepository categoryRepository;

    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime rangeStartDateTime = LocalDateTime.parse(rangeStart);
        LocalDateTime rangeEndDateTime = LocalDateTime.parse(rangeEnd);
        return eventRepository.findAllEvents(users, states, categories, rangeStartDateTime, rangeEndDateTime, PageRequest.of(from/size, size)).stream()
                .map(eventDtoMapper::mapEventToFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие id=" + eventId + " не найдено"));

        if(LocalDateTime.now().isAfter(event.getEventDate().minus(1, ChronoUnit.HOURS))) {
            throw new ConflictException("До начала события меньше часа, изменение невозможно");
        }
        if(!event.getState().equals(EventState.PENDING.toString())) {
            throw new ConflictException("Событие не в состоянии ожидания публикации");
        }
        if((!updateRequest.getStateAction().equals(StateAction.REJECT_EVENT.toString())
                && event.getState().equals(EventState.PUBLISHED.toString()))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }
        event = updateEventWithAdminRequest(event, updateRequest);
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
    }

    private Event updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) {
        if(updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if(updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if(updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if(updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if(updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if(updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if(updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if(updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверный аргумент для публикации/отклонения события")
            }
        }
        if(updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
        return event;
    }
}
