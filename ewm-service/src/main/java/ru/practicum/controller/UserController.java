package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserShortDto> getUsers(@RequestParam List<Long> ids,
                                       @RequestParam(required = false) Long from,
                                       @RequestParam(required = false) Long size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    public UserShortDto createUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
