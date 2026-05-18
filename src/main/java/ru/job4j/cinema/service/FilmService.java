package ru.job4j.cinema.service;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.dto.FilmDto;

public interface FilmService {
    List<FilmDto> findAll();

    Optional<FilmDto> findById(int id);
}
