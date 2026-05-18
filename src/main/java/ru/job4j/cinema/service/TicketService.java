package ru.job4j.cinema.service;

import java.util.Optional;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;

public interface TicketService {
    Optional<Ticket> buy(Ticket ticket);

    Optional<TicketDto> findById(int id);
}
