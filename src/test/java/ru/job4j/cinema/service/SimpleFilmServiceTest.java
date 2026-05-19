package ru.job4j.cinema.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmServiceTest {

    @DisplayName("сервис собирает FilmDto с названием жанра из отдельного репозитория.")
    @Test
    void whenFindAllThenReturnFilmDtosWithGenreName() {
        var filmRepository = mock(FilmRepository.class);
        var genreRepository = mock(GenreRepository.class);
        var film = new Film("Матрица", "Описание", 1999, 1, 16, 136, 1);
        film.setId(1);
        when(filmRepository.findAll()).thenReturn(List.of(film));
        when(genreRepository.findById(1)).thenReturn(Optional.of(new Genre(1, "Фантастика")));
        var filmService = new SimpleFilmService(filmRepository, genreRepository);

        var films = filmService.findAll();

        assertThat(films).hasSize(1);
        assertThat(films.get(0).getId()).isEqualTo(1);
        assertThat(films.get(0).getName()).isEqualTo("Матрица");
        assertThat(films.get(0).getGenre()).isEqualTo("Фантастика");
        assertThat(films.get(0).getFileId()).isEqualTo(1);
    }

    @DisplayName("если фильм не найден, сервис возвращает Optional.empty().")
    @Test
    void whenFindAbsentFilmByIdThenReturnEmptyOptional() {
        var filmRepository = mock(FilmRepository.class);
        var genreRepository = mock(GenreRepository.class);
        when(filmRepository.findById(1)).thenReturn(Optional.empty());
        var filmService = new SimpleFilmService(filmRepository, genreRepository);

        var film = filmService.findById(1);

        assertThat(film).isEmpty();
    }
}
