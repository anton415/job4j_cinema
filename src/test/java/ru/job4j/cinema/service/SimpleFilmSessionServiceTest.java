package ru.job4j.cinema.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.GenreRepository;
import ru.job4j.cinema.repository.HallRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmSessionServiceTest {

    @DisplayName("сервис собирает расписание с фильмом, жанром, залом, временем и ценой.")
    @Test
    void whenFindAllThenReturnFilmSessionDtos() {
        var context = createContext();
        when(context.filmSessionRepository.findAll()).thenReturn(List.of(context.session));
        when(context.filmRepository.findById(1)).thenReturn(Optional.of(context.film));
        when(context.genreRepository.findById(1)).thenReturn(Optional.of(new Genre(1, "Фантастика")));
        when(context.hallRepository.findById(1)).thenReturn(Optional.of(context.hall));

        var sessions = context.service.findAll();

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getFilmName()).isEqualTo("Матрица");
        assertThat(sessions.get(0).getGenre()).isEqualTo("Фантастика");
        assertThat(sessions.get(0).getHallName()).isEqualTo("Красный зал");
        assertThat(sessions.get(0).getPrice()).isEqualTo(350);
    }

    @DisplayName("сервис формирует номера рядов и мест по размерам зала.")
    @Test
    void whenFindRowsAndPlacesThenUseHallSize() {
        var context = createContext();
        when(context.filmSessionRepository.findById(1)).thenReturn(Optional.of(context.session));
        when(context.hallRepository.findById(1)).thenReturn(Optional.of(context.hall));

        var rows = context.service.findRowsBySessionId(1);
        var places = context.service.findPlacesBySessionId(1);

        assertThat(rows).containsExactly(1, 2, 3);
        assertThat(places).containsExactly(1, 2, 3, 4);
    }

    private TestContext createContext() {
        var filmSessionRepository = mock(FilmSessionRepository.class);
        var filmRepository = mock(FilmRepository.class);
        var genreRepository = mock(GenreRepository.class);
        var hallRepository = mock(HallRepository.class);
        var service = new SimpleFilmSessionService(filmSessionRepository, filmRepository,
                genreRepository, hallRepository);
        return new TestContext(service, filmSessionRepository, filmRepository,
                genreRepository, hallRepository);
    }

    private static class TestContext {
        private final SimpleFilmSessionService service;
        private final FilmSessionRepository filmSessionRepository;
        private final FilmRepository filmRepository;
        private final GenreRepository genreRepository;
        private final HallRepository hallRepository;
        private final FilmSession session = new FilmSession(1, 1, 1,
                LocalDateTime.of(2026, 5, 20, 10, 0),
                LocalDateTime.of(2026, 5, 20, 12, 16), 350);
        private final Film film = new Film("Матрица", "Описание", 1999, 1, 16, 136, 1);
        private final Hall hall = new Hall(1, "Красный зал", 3, 4, "Описание");

        TestContext(SimpleFilmSessionService service, FilmSessionRepository filmSessionRepository,
                    FilmRepository filmRepository, GenreRepository genreRepository,
                    HallRepository hallRepository) {
            this.service = service;
            this.filmSessionRepository = filmSessionRepository;
            this.filmRepository = filmRepository;
            this.genreRepository = genreRepository;
            this.hallRepository = hallRepository;
            this.film.setId(1);
        }
    }
}
