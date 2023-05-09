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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "true") Boolean pinned,
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
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Запрос: Получение категорий");
        return eventService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Запрос: Получение информации о категории по её идентификатору");
        return eventService.getCategoryById(catId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsWithFilters(@RequestParam(required = false) String text,
                                                    @RequestParam(required = false) List<Integer> categories,
                                                    @RequestParam(required = false) Boolean paid,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(required = false) String sort,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size,
                                                    HttpServletRequest request) {
        log.info("Запрос: Получение событий с возможностью фильтрации");
        return eventService.getEventsWithFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable Long id) {
        log.info("Запрос: Получение подробной информации об опубликованном событии по его идентификатору");
        return eventService.getEventDtoById(id);
    }
}
