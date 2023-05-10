package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dtoMapper.UserDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        return userRepository.findAllByIdsPageable(ids, PageRequest.of(from / size, size)).stream()
                .map(user -> userDtoMapper.mapUserToDto(user))
                .collect(Collectors.toList());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("не удалось получить данные пользователя", "Пользователь id=" + userId + " не найден"));
    }

    public UserDto addUser(NewUserRequest newUserDto) {
        if (userRepository.findByName(newUserDto.getName()).size() > 0) {
            throw new ConflictException("не удалось создать пользователя", "имя (" + newUserDto.getName() + ") уже занято");
        }
        User savedUser = userRepository.save(userDtoMapper.mapNewUserRequestToUser(newUserDto));
        log.info("Пользователь сохранен с id=" + savedUser.getId());
        return userDtoMapper.mapUserToDto(savedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь id=" + userId + " удален");
    }
}
