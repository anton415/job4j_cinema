package ru.job4j.cinema.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class Sql2oFilmSessionRepositoryTest {
    @Autowired
    private FilmSessionRepository filmSessionRepository;

    /**
     * Сценарий: репозиторий читает сеансы, загруженные Liquibase DML-скриптами.
     */
    @Test
    void whenFindAllThenReturnSeededFilmSessions() {
        var sessions = filmSessionRepository.findAll();

        assertThat(sessions).hasSize(4);
        assertThat(sessions.get(0).getId()).isEqualTo(1);
        assertThat(sessions.get(0).getFilmId()).isEqualTo(1);
        assertThat(sessions.get(0).getHallId()).isEqualTo(1);
    }

    /**
     * Сценарий: сеанс находится по id с корректным маппингом времени и цены.
     */
    @Test
    void whenFindByIdThenReturnFilmSession() {
        var session = filmSessionRepository.findById(3);

        assertThat(session).isPresent();
        assertThat(session.get().getHallId()).isEqualTo(2);
        assertThat(session.get().getStartTime())
                .isEqualTo(LocalDateTime.of(2026, 5, 20, 18, 30));
        assertThat(session.get().getPrice()).isEqualTo(390);
    }
}
