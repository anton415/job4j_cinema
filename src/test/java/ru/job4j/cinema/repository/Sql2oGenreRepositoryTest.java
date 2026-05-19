package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oGenreRepositoryTest {
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        genreRepository = new Sql2oGenreRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("репозиторий читает жанры, загруженные SQL DML-скриптами.")
    @Test
    void whenFindAllThenReturnSeededGenres() {
        var genres = genreRepository.findAll();

        assertThat(genres).hasSize(3);
        assertThat(genres.get(0).getName()).isEqualTo("Фантастика");
        assertThat(genres.get(1).getName()).isEqualTo("Драма");
        assertThat(genres.get(2).getName()).isEqualTo("Триллер");
    }

    @DisplayName("жанр находится по id.")
    @Test
    void whenFindByIdThenReturnGenre() {
        var genre = genreRepository.findById(2);

        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Драма");
    }
}
