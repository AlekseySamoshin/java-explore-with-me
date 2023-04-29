package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserDtoMapper;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserRepository userRepository;
    UserDtoMapper userDtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserShortDto> getUsers(List<Long> ids, Long from, Long size) {
        return userRepository.findAllById(ids).stream()
                .map(user -> userDtoMapper.mapUserToShortDto(user))
                .collect(Collectors.toList());
    }

    public UserShortDto addUser(UserDto userDto) {
        User savedUser = userRepository.save(userDtoMapper.mapDtoToUser(userDto));
        return userDtoMapper.mapUserToShortDto(savedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
