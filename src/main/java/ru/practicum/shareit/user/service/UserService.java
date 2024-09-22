package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getById(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с id = %d не найден", userId);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    public UserDto create(NewUserRequest request) {
        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            log.error("Creating user is failed. email = {} exists", request.getEmail());
            throw new DuplicatedDataException("User with email = " + request.getEmail() + " exists");
        }

        User user = userMapper.toUser(request);
        user = userRepository.save(user);
        log.info("Creating user is successful: {}", user);
        return userMapper.toDto(user);
    }

    public UserDto update(Long userId, UpdateUserRequest request) {
        User updatedUser = getUserById(userId);
        Optional<User> alreadyExistUser = userRepository.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            log.error("Updating user is failed. email = {} exists", request.getEmail());
            throw new DuplicatedDataException("User with email = " + request.getEmail() + " exists");
        }

        userMapper.updateUser(updatedUser, request);
        updatedUser = userRepository.save(updatedUser);
        log.info("Updating user is successful: {}", updatedUser);
        return userMapper.toDto(updatedUser);
    }

    public void removeUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}
