package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    User getUserById(Long userId);

    UserDto addUser(NewUserRequest newUserDto);

    void deleteUser(Long userId);
}
