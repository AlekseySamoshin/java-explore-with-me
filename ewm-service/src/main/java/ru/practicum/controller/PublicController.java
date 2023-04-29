package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
public class PublicController {

    EventService eventService;

    @Autowired
    public PublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam Boolean pinned,
                                          @RequestParam Integer from,
                                          @RequestParam Integer size) {
//    Получение подборок событий
        return eventService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
//    Получение подборки событий по его id
        return eventService.getCompilationById(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam Integer from,
                                     @RequestParam Integer size) {
//    Получение категорий
        return eventService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
//    Получение информации о категории по её идентификатору
        return eventService.getCategoryById(catId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsWithFilters(@RequestParam String text,
                                                    @RequestParam List<Integer> categories,
                                                    @RequestParam Boolean paid,
                                                    @RequestParam String rangeStart,
                                                    @RequestParam String rangeEnd,
                                                    @RequestParam Boolean onlyAvailable,
                                                    @RequestParam String sort,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
//    это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
//    текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
//    если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
//    информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
//    информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

//    Получение событий с возможностью фильтрации
        return eventService.getEventsWithFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, sizes);
    }

    @GetMapping("/events/{id}")
//    событие должно быть опубликовано
//    информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
//    информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    public EventFullDto getEventById(@PathVariable Long id) {
//    Получение подробной информации об опубликованном событии по его идентификатору
        return eventService.getEventById(id);
    }
}
