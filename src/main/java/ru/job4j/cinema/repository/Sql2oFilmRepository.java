package ru.job4j.cinema.repository;

import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.Film;

@ThreadSafe
@Repository
public class Sql2oFilmRepository implements FilmRepository {
    private static final String SELECT_FILMS = """
            SELECT id,
                   name,
                   description,
                   "year",
                   genre_id AS genreId,
                   minimal_age AS minimalAge,
                   duration_in_minutes AS durationInMinutes,
                   file_id AS fileId
            FROM films
            """;
    private static final String FIND_ALL = SELECT_FILMS + """
            ORDER BY id
            """;
    private static final String FIND_BY_ID = SELECT_FILMS + """
            WHERE id = :id
            """;

    private final Sql2o sql2o;

    public Sql2oFilmRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public List<Film> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery(FIND_ALL).executeAndFetch(Film.class);
        }
    }

    @Override
    public Optional<Film> findById(int id) {
        try (var connection = sql2o.open()) {
            var film = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Film.class);
            return Optional.ofNullable(film);
        }
    }
}
