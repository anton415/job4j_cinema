package ru.job4j.cinema.service;

import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Service;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;
import ru.job4j.cinema.repository.TicketRepository;
import ru.job4j.cinema.repository.UserRepository;

@ThreadSafe
@Service
public class SimpleTicketService implements TicketService {
    private final TicketRepository ticketRepository;
    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;

    public SimpleTicketService(TicketRepository ticketRepository,
                               FilmSessionRepository filmSessionRepository,
                               FilmRepository filmRepository,
                               HallRepository hallRepository,
                               UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Ticket> buy(Ticket ticket) {
        var sessionOptional = filmSessionRepository.findById(ticket.getSessionId());
        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }
        var hallOptional = hallRepository.findById(sessionOptional.get().getHallId());
        if (hallOptional.isEmpty() || userRepository.findById(ticket.getUserId()).isEmpty()) {
            return Optional.empty();
        }
        if (!isPlaceInHall(ticket, hallOptional.get())) {
            return Optional.empty();
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Optional<TicketDto> findById(int id) {
        return ticketRepository.findById(id).flatMap(this::toDto);
    }

    private Optional<TicketDto> toDto(Ticket ticket) {
        var sessionOptional = filmSessionRepository.findById(ticket.getSessionId());
        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }
        return toDto(ticket, sessionOptional.get());
    }

    private Optional<TicketDto> toDto(Ticket ticket, FilmSession session) {
        var filmOptional = filmRepository.findById(session.getFilmId());
        var hallOptional = hallRepository.findById(session.getHallId());
        var userOptional = userRepository.findById(ticket.getUserId());
        if (filmOptional.isEmpty() || hallOptional.isEmpty() || userOptional.isEmpty()) {
            return Optional.empty();
        }
        var dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setSessionId(session.getId());
        dto.setFilmName(filmOptional.get().getName());
        dto.setHallName(hallOptional.get().getName());
        dto.setStartTime(session.getStartTime());
        dto.setRowNumber(ticket.getRowNumber());
        dto.setPlaceNumber(ticket.getPlaceNumber());
        dto.setPrice(session.getPrice());
        dto.setUserFullName(userOptional.get().getFullName());
        return Optional.of(dto);
    }

    private boolean isPlaceInHall(Ticket ticket, Hall hall) {
        return ticket.getRowNumber() >= 1
                && ticket.getRowNumber() <= hall.getRowCount()
                && ticket.getPlaceNumber() >= 1
                && ticket.getPlaceNumber() <= hall.getPlaceCount();
    }
}
