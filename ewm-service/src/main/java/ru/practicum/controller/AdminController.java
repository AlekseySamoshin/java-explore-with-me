package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.service.CompilationService;
import ru.practicum.service.EventService;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
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
                                       @RequestParam(required = false, defaultValue = "0") Long from,
                                       @RequestParam(required = false, defaultValue = "10") Long size) {

//        Получение информации о пользователях
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody NewUserRequest newUserDto) {
        return userService.addUser(newUserDto);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
//        Успешный ответ: код 204
//        Удаление пользователя
        userService.deleteUser(userId);
    }





//__________________________ C A T E G O R I E S _____________________________




    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody NewCategoryDto categoryDto) {
//        имя категории должно быть уникальным

//        Добавление новой категории
        return eventService.createCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
//        с категорией не должно быть связано ни одного события.

//        Успешный ответ: код 204
        eventService.deleteCategoryById(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody CategoryDto categoryDto) {

//        Изменение категории
        return eventService.updateCategory(catId, categoryDto);
    }




//__________________________ E V E N T S _____________________________

    @GetMapping("/events")
    public EventFullDto getEvents(@RequestParam List<Long> users,
                                  @RequestParam List<String> states,
                                  @RequestParam List<Long> categories,
                                  @RequestParam String rangeStart,
                                  @RequestParam String rangeEnd,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
//        Поиск событий
        return eventService.getEvents(users, states,categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminRequest eventFullDto) {
//        дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
//        событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
//        событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)

//        Редактирование данных события и его статуса (отклонение/публикация).
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
        return compilationService.updateCompilation(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }
}
