package ru.job4j.cinema.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class Sql2oFilmRepositoryTest {
    @Autowired
    private FilmRepository filmRepository;

    /**
     * Сценарий: репозиторий читает фильмы, загруженные Liquibase DML-скриптами.
     */
    @Test
    void whenFindAllThenReturnSeededFilms() {
        var films = filmRepository.findAll();

        assertThat(films).hasSize(3);
        assertThat(films.get(0).getId()).isEqualTo(1);
        assertThat(films.get(0).getName()).isEqualTo("Матрица");
        assertThat(films.get(0).getGenreId()).isEqualTo(1);
        assertThat(films.get(0).getFileId()).isEqualTo(1);
    }

    /**
     * Сценарий: фильм находится по id с корректным маппингом SQL alias в Java-поля.
     */
    @Test
    void whenFindByIdThenReturnFilm() {
        var film = filmRepository.findById(2);

        assertThat(film).isPresent();
        assertThat(film.get().getName()).isEqualTo("Интерстеллар");
        assertThat(film.get().getDurationInMinutes()).isEqualTo(169);
    }
}
