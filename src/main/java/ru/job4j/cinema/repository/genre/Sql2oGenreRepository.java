package ru.job4j.cinema.repository.genre;

import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.Genre;

@ThreadSafe
@Repository
public class Sql2oGenreRepository implements GenreRepository {
    private static final String FIND_ALL = """
            SELECT id, name
            FROM genres
            ORDER BY id
            """;
    private static final String FIND_BY_ID = """
            SELECT id, name
            FROM genres
            WHERE id = :id
            """;

    private final Sql2o sql2o;

    public Sql2oGenreRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public List<Genre> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery(FIND_ALL).executeAndFetch(Genre.class);
        }
    }

    @Override
    public Optional<Genre> findById(int id) {
        try (var connection = sql2o.open()) {
            var genre = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Genre.class);
            return Optional.ofNullable(genre);
        }
    }
}
