package ru.job4j.cinema.service.ticket;

import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Service;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.mapper.TicketMapper;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.filmsession.FilmSessionRepository;
import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.repository.user.UserRepository;

@ThreadSafe
@Service
public class SimpleTicketService implements TicketService {
    private final TicketRepository ticketRepository;
    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    public SimpleTicketService(TicketRepository ticketRepository,
                               FilmSessionRepository filmSessionRepository,
                               FilmRepository filmRepository,
                               HallRepository hallRepository,
                               UserRepository userRepository,
                               TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
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
        return Optional.of(ticketMapper.toDto(ticket, session, filmOptional.get(),
                hallOptional.get(), userOptional.get()));
    }

    private boolean isPlaceInHall(Ticket ticket, Hall hall) {
        return ticket.getRowNumber() >= 1
                && ticket.getRowNumber() <= hall.getRowCount()
                && ticket.getPlaceNumber() >= 1
                && ticket.getPlaceNumber() <= hall.getPlaceCount();
    }
}
