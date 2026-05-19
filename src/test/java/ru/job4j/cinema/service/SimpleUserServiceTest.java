package ru.job4j.cinema.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleUserServiceTest {

    @DisplayName("регистрация делегируется репозиторию пользователей.")
    @Test
    void whenRegisterThenReturnSavedUser() {
        var userRepository = mock(UserRepository.class);
        var user = new User(1, "Иван Иванов", "ivan@example.com", "password");
        when(userRepository.save(user)).thenReturn(Optional.of(user));
        var userService = new SimpleUserService(userRepository);

        var saved = userService.register(user);

        assertThat(saved).contains(user);
    }

    @DisplayName("вход ищет пользователя по email и password.")
    @Test
    void whenLoginThenReturnUser() {
        var userRepository = mock(UserRepository.class);
        var user = new User(1, "Иван Иванов", "ivan@example.com", "password");
        when(userRepository.findByEmailAndPassword("ivan@example.com", "password"))
                .thenReturn(Optional.of(user));
        var userService = new SimpleUserService(userRepository);

        var logged = userService.login("ivan@example.com", "password");

        assertThat(logged).contains(user);
    }
}
