package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@Slf4j
public class AdminController {
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @Autowired
    public AdminController(UserService userService,
                           EventService eventService,
                           CompilationService compilationService) {
        this.userService = userService;
        this.eventService = eventService;
        this.compilationService = compilationService;
    }



//__________________________ U S E R S _____________________________



    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                       @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Запрос: Получение информации о пользователях");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody NewUserRequest newUserDto) {
        log.info("Запрос: Создание нового пользователя");
        return userService.addUser(newUserDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Запрос: Удаление пользователя");
        userService.deleteUser(userId);
    }



//__________________________ C A T E G O R I E S _____________________________



    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody NewCategoryDto categoryDto) {
        log.info("Запрос: Добавление новой категории");
        return eventService.addCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Запрос: Удаление категории id=" + catId);
//        Успешный ответ: код 204
        eventService.deleteCategoryById(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody CategoryDto categoryDto) {

        log.info("Запрос: Изменение категории id=" + catId);
        return eventService.updateCategory(catId, categoryDto);
    }


//__________________________ E V E N T S _____________________________

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam List<Long> users,
                                  @RequestParam(required = false) List<String> states,
                                  @RequestParam(required = false) List<Long> categories,
                                  @RequestParam(required = false) String rangeStart,
                                  @RequestParam(required = false) String rangeEnd,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Запрос: Поиск событий");
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequest eventFullDto) {
        log.info("Запрос: Редактирование данных события и его статуса");
        return eventService.updateEvent(eventId, eventFullDto);
    }



//__________________________ C O M P I L A T I O N S _____________________________



    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody NewCompilationDto compilationDto) {

//        Добавление новой подборки (подборка может не содержать событий)
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody UpdateCompilationRequest compilationDto) {

//        Обновить информацию о подборке
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }
}
