package ru.job4j.cinema.repository.film;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.model.Film;

public interface FilmRepository {
    List<Film> findAll();

    Optional<Film> findById(int id);
}
