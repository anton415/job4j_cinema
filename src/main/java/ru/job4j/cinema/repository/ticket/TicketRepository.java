package ru.job4j.cinema.repository.ticket;

import java.util.List;
import java.util.Optional;

import ru.job4j.cinema.model.Ticket;

public interface TicketRepository {
    Optional<Ticket> save(Ticket ticket);

    Optional<Ticket> findById(int id);

    List<Ticket> findBySessionId(int sessionId);
}
