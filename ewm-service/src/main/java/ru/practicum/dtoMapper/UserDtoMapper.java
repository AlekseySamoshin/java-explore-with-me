package ru.practicum.dtoMapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

@Component
public class UserDtoMapper {
    public UserDto mapUserToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public UserShortDto mapUserToShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public User mapDtoToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getName()
        );
    }

    public User mapNewUserRequestToUser(NewUserRequest newUser) {
        return new User(
                null,
                newUser.getEmail(),
                newUser.getName()
        );
    }
}
