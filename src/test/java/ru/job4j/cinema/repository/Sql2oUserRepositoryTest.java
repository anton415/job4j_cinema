package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oUserRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        userRepository = new Sql2oUserRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("пользователь сохраняется и затем находится по email и password.")
    @Test
    void whenSaveUserThenCanFindByEmailAndPassword() {
        var user = new User("Иван Иванов", "ivan@example.com", "password");

        var saved = userRepository.save(user);
        var found = userRepository.findByEmailAndPassword("ivan@example.com", "password");

        assertThat(saved).isPresent();
        assertThat(saved.get().getId()).isEqualTo(1);
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved.get());
    }

    @DisplayName("повторная регистрация с тем же email возвращает Optional.empty().")
    @Test
    void whenSaveUserWithDuplicateEmailThenReturnEmptyOptional() {
        var first = new User("Иван Иванов", "ivan@example.com", "password");
        var second = new User("Петр Петров", "ivan@example.com", "password");

        var saved = userRepository.save(first);
        var duplicate = userRepository.save(second);

        assertThat(saved).isPresent();
        assertThat(duplicate).isEmpty();
    }
}
