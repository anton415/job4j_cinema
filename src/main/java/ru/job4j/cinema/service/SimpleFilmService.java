package ru.job4j.cinema.service;

import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Service;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

@ThreadSafe
@Service
public class SimpleFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    public SimpleFilmService(FilmRepository filmRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public List<FilmDto> findAll() {
        return filmRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        return filmRepository.findById(id).map(this::toDto);
    }

    private FilmDto toDto(Film film) {
        var dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setYear(film.getYear());
        dto.setMinimalAge(film.getMinimalAge());
        dto.setDurationInMinutes(film.getDurationInMinutes());
        dto.setGenre(findGenreName(film.getGenreId()));
        dto.setFileId(film.getFileId());
        return dto;
    }

    private String findGenreName(int genreId) {
        return genreRepository.findById(genreId)
                .map(Genre::getName)
                .orElse("");
    }
}
