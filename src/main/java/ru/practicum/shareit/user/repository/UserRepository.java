package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    User create(User user);

    User update(User updatedUser);

    void remove(User user);
}
