package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventAdminRequest;

import java.util.List;

@Service
public class EventService {
    public EventFullDto getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer to) {
        return new EventFullDto();
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventFullDto) {
//        дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
//        событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
//        событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        return new EventFullDto();
    }

    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        return categoryDto;
    }

    public void deleteCategoryById(Long catId) {
        return;
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        return new CategoryDto();
    }
}
