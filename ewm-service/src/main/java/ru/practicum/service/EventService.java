package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dtoMapper.CategoryDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {
    CategoryDtoMapper categoryDtoMapper;
    EventRepository eventRepository;
    CategoryRepository categoryRepository;

    @Autowired
    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        categoryDtoMapper = new CategoryDtoMapper();
    }

    public EventFullDto getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer to) {
        return new EventFullDto();
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventFullDto) {
//        дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
//        событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
//        событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        return new EventFullDto();
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        categoryRepository.save(categoryDtoMapper.mapNewDtoToCategory(newCategoryDto));
    }

    public void deleteCategoryById(Long catId) {
        if(categoryIsEmpty(catId)) {
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
        if(categoryDto.getName() == null || categoryDto.getName().isBlank()) {
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
}
