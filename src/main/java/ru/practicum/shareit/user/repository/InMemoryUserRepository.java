package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.IdentifierGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final HashMap<Long, User> users;
    private final IdentifierGenerator identifierGenerator;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User create(User user) {
        Long userId = identifierGenerator.getNextId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(User updatedUser) {
        return users.put(updatedUser.getId(), updatedUser);
    }

    @Override
    public void remove(User user) {
        users.remove(user.getId());
    }
}
