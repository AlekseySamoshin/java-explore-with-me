package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@Slf4j
public class PublicController {

    EventService eventService;
    CompilationService compilationService;

    @Autowired
    public PublicController(EventService eventService, CompilationService compilationService) {
        this.eventService = eventService;
        this.compilationService = compilationService;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос: Получение подборок событий");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Запрос: Получение подборки событий по его id");
        return compilationService.getCompilationById(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam Integer from,
                                           @RequestParam Integer size) {
        log.info("Запрос: Получение категорий");
        return eventService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Запрос: Получение информации о категории по её идентификатору");
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
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
//    это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
//    текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
//    если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
//    информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
//    информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        log.info("Запрос: Получение событий с возможностью фильтрации");
        return eventService.getEventsWithFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
//    событие должно быть опубликовано
//    информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
//    информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    public EventFullDto getEventById(@PathVariable Long id) {
        log.info("Запрос: Получение подробной информации об опубликованном событии по его идентификатору");
        return eventService.getEventById(id);
    }
}
