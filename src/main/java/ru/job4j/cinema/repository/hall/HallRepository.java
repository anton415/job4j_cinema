package ru.job4j.cinema.repository.hall;

import java.util.Optional;

import ru.job4j.cinema.model.Hall;

public interface HallRepository {
    Optional<Hall> findById(int id);
}
