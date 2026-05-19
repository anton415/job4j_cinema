package ru.job4j.cinema.service.filmsession;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.dto.FilmSessionDto;

public interface FilmSessionService {
    List<FilmSessionDto> findAll();

    Optional<FilmSessionDto> findById(int id);

    List<Integer> findRowsBySessionId(int sessionId);

    List<Integer> findPlacesBySessionId(int sessionId);
}
