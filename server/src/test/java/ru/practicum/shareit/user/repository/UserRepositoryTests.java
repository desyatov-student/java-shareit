package ru.practicum.shareit.user.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.helpers.TestData;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTests {

    private final UserRepository repository;

    @Test
    void getById_returnEntity_userIsExisted() {
        // Given
        User initialUser = TestData.createUser();
        Long userId = repository.save(initialUser).getId();

        // When
        Optional<User> userOptional = repository.findById(userId);

        // Then
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", userId);
                    assertThat(user).hasFieldOrPropertyWithValue("email", initialUser.getEmail());
                    assertThat(user).hasFieldOrPropertyWithValue("name", initialUser.getName());
                });
    }

}

