package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.film.Sql2oFilmRepository;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmRepositoryTest {
    private FilmRepository filmRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        filmRepository = new Sql2oFilmRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("репозиторий читает фильмы, загруженные SQL DML-скриптами.")
    @Test
    void whenFindAllThenReturnSeededFilms() {
        var films = filmRepository.findAll();

        assertThat(films).hasSize(3);
        assertThat(films.get(0).getId()).isEqualTo(1);
        assertThat(films.get(0).getName()).isEqualTo("Матрица");
        assertThat(films.get(0).getGenreId()).isEqualTo(1);
        assertThat(films.get(0).getFileId()).isEqualTo(1);
    }

    @DisplayName("фильм находится по id с корректным маппингом SQL alias в Java-поля.")
    @Test
    void whenFindByIdThenReturnFilm() {
        var film = filmRepository.findById(2);

        assertThat(film).isPresent();
        assertThat(film.get().getName()).isEqualTo("Интерстеллар");
        assertThat(film.get().getDurationInMinutes()).isEqualTo(169);
    }
}
