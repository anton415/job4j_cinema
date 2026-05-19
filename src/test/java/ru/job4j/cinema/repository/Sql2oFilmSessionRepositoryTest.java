package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmSessionRepositoryTest {
    private FilmSessionRepository filmSessionRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        filmSessionRepository = new Sql2oFilmSessionRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("репозиторий читает сеансы, загруженные SQL DML-скриптами.")
    @Test
    void whenFindAllThenReturnSeededFilmSessions() {
        var sessions = filmSessionRepository.findAll();

        assertThat(sessions).hasSize(4);
        assertThat(sessions.get(0).getId()).isEqualTo(1);
        assertThat(sessions.get(0).getFilmId()).isEqualTo(1);
        assertThat(sessions.get(0).getHallId()).isEqualTo(1);
    }

    @DisplayName("сеанс находится по id с корректным маппингом времени и цены.")
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
