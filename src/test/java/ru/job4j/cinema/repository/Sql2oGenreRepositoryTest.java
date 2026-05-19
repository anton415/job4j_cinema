package ru.job4j.cinema.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Tag("testcontainers")
@Testcontainers
@Import(PostgresContainerConfiguration.class)
class Sql2oGenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("репозиторий читает жанры, загруженные Liquibase DML-скриптами.")
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
