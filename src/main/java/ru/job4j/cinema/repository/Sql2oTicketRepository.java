package ru.job4j.cinema.repository;

import java.util.List;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.Ticket;

@ThreadSafe
@Repository
public class Sql2oTicketRepository implements TicketRepository {
    private static final String SAVE = """
            INSERT INTO tickets (session_id, row_number, place_number, user_id)
            VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
            """;
    private static final String SELECT_TICKETS = """
            SELECT id,
                   session_id AS sessionId,
                   row_number AS rowNumber,
                   place_number AS placeNumber,
                   user_id AS userId
            FROM tickets
            """;
    private static final String FIND_BY_ID = SELECT_TICKETS + """
            WHERE id = :id
            """;
    private static final String FIND_BY_SESSION_ID = SELECT_TICKETS + """
            WHERE session_id = :sessionId
            ORDER BY row_number, place_number, id
            """;

    private final Sql2o sql2o;

    public Sql2oTicketRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (var connection = sql2o.beginTransaction()) {
            var id = (Number) connection.createQuery(SAVE, true)
                    .bind(ticket)
                    .executeUpdate()
                    .getKey();
            ticket.setId(id.intValue());
            connection.commit();
            return Optional.of(ticket);
        } catch (Sql2oException exception) {
            if (Sql2oExceptionHelper.isUniqueViolation(exception)) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    @Override
    public Optional<Ticket> findById(int id) {
        try (var connection = sql2o.open()) {
            var ticket = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }

    @Override
    public List<Ticket> findBySessionId(int sessionId) {
        try (var connection = sql2o.open()) {
            return connection.createQuery(FIND_BY_SESSION_ID)
                    .addParameter("sessionId", sessionId)
                    .executeAndFetch(Ticket.class);
        }
    }
}
