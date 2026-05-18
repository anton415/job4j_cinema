package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.job4j.cinema.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class Sql2oUserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sql2oTestHelper sql2oTestHelper;

    @BeforeEach
    void setUp() {
        sql2oTestHelper.clearMutableTables();
    }

    /**
     * Сценарий: пользователь сохраняется и затем находится по email и password.
     */
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

    /**
     * Сценарий: повторная регистрация с тем же email возвращает Optional.empty().
     */
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
