package ru.job4j.cinema.repository.genre;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.model.Genre;

public interface GenreRepository {
    List<Genre> findAll();

    Optional<Genre> findById(int id);
}
