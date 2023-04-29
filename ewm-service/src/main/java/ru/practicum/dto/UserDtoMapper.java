package ru.practicum.dto;

import ru.practicum.model.User;

public class UserDtoMapper {
    public UserDto mapUserToDto(User user) {
        return new UserDto();
    }

    public UserShortDto mapUserToShortDto(User user) {
        return new UserShortDto();
    }


    public User mapDtoToUser(UserDto userDto) {
        return new User();
    }
}
