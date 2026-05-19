package ru.job4j.cinema.repository.hall;

import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.Hall;

@ThreadSafe
@Repository
public class Sql2oHallRepository implements HallRepository {
    private static final String FIND_BY_ID = """
            SELECT id,
                   name,
                   row_count AS rowCount,
                   place_count AS placeCount,
                   description
            FROM halls
            WHERE id = :id
            """;

    private final Sql2o sql2o;

    public Sql2oHallRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (var connection = sql2o.open()) {
            var hall = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Hall.class);
            return Optional.ofNullable(hall);
        }
    }
}
