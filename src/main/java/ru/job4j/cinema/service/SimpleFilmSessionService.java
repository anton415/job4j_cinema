package ru.job4j.cinema.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Service;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.GenreRepository;
import ru.job4j.cinema.repository.HallRepository;

@ThreadSafe
@Service
public class SimpleFilmSessionService implements FilmSessionService {
    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final HallRepository hallRepository;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository,
                                    FilmRepository filmRepository,
                                    GenreRepository genreRepository,
                                    HallRepository hallRepository) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public List<FilmSessionDto> findAll() {
        return filmSessionRepository.findAll().stream()
                .map(this::toDto)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public Optional<FilmSessionDto> findById(int id) {
        return filmSessionRepository.findById(id).flatMap(this::toDto);
    }

    @Override
    public List<Integer> findRowsBySessionId(int sessionId) {
        return findHallBySessionId(sessionId)
                .map(hall -> createNumbers(hall.getRowCount()))
                .orElse(List.of());
    }

    @Override
    public List<Integer> findPlacesBySessionId(int sessionId) {
        return findHallBySessionId(sessionId)
                .map(hall -> createNumbers(hall.getPlaceCount()))
                .orElse(List.of());
    }

    private Optional<FilmSessionDto> toDto(FilmSession session) {
        var filmOptional = filmRepository.findById(session.getFilmId());
        var hallOptional = hallRepository.findById(session.getHallId());
        if (filmOptional.isEmpty() || hallOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(createDto(session, filmOptional.get(), hallOptional.get()));
    }

    private FilmSessionDto createDto(FilmSession session, Film film, Hall hall) {
        var dto = new FilmSessionDto();
        dto.setId(session.getId());
        dto.setFilmId(film.getId());
        dto.setFilmName(film.getName());
        dto.setGenre(findGenreName(film.getGenreId()));
        dto.setHallName(hall.getName());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setPrice(session.getPrice());
        dto.setFileId(film.getFileId());
        return dto;
    }

    private Optional<Hall> findHallBySessionId(int sessionId) {
        return filmSessionRepository.findById(sessionId)
                .flatMap(session -> hallRepository.findById(session.getHallId()));
    }

    private List<Integer> createNumbers(int count) {
        return IntStream.rangeClosed(1, count).boxed().toList();
    }

    private String findGenreName(int genreId) {
        return genreRepository.findById(genreId)
                .map(Genre::getName)
                .orElse("");
    }
}
