package ru.job4j.cinema.repository.filmsession;

import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.FilmSession;

@ThreadSafe
@Repository
public class Sql2oFilmSessionRepository implements FilmSessionRepository {
    private static final String SELECT_SESSIONS = """
            SELECT id,
                   film_id AS filmId,
                   halls_id AS hallId,
                   start_time AS startTime,
                   end_time AS endTime,
                   price
            FROM film_sessions
            """;
    private static final String FIND_ALL = SELECT_SESSIONS + """
            ORDER BY start_time, id
            """;
    private static final String FIND_BY_ID = SELECT_SESSIONS + """
            WHERE id = :id
            """;

    private final Sql2o sql2o;

    public Sql2oFilmSessionRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public List<FilmSession> findAll() {
        try (var connection = sql2o.open()) {
            return connection.createQuery(FIND_ALL).executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        try (var connection = sql2o.open()) {
            var session = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(FilmSession.class);
            return Optional.ofNullable(session);
        }
    }
}
