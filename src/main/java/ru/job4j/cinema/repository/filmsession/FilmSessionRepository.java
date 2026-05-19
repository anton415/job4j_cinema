package ru.job4j.cinema.repository.filmsession;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.model.FilmSession;

public interface FilmSessionRepository {
    List<FilmSession> findAll();

    Optional<FilmSession> findById(int id);
}
