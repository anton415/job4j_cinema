package ru.job4j.cinema.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.filmsession.FilmSessionRepository;
import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.repository.user.UserRepository;
import ru.job4j.cinema.service.ticket.SimpleTicketService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SimpleTicketServiceTest {

    @DisplayName("покупка проходит, когда сеанс, зал, пользователь и место корректны.")
    @Test
    void whenBuyValidTicketThenReturnSavedTicket() {
        var context = createContext();
        var ticket = new Ticket(1, 2, 3, 1);
        var savedTicket = new Ticket(1, 1, 2, 3, 1);
        prepareValidPurchase(context, ticket);
        when(context.ticketRepository.save(ticket)).thenReturn(Optional.of(savedTicket));

        var result = context.service.buy(ticket);

        assertThat(result).contains(savedTicket);
    }

    @DisplayName("если место уже занято, сервис возвращает Optional.empty().")
    @Test
    void whenBuyOccupiedPlaceThenReturnEmptyOptional() {
        var context = createContext();
        var ticket = new Ticket(1, 2, 3, 1);
        prepareValidPurchase(context, ticket);
        when(context.ticketRepository.save(ticket)).thenReturn(Optional.empty());

        var result = context.service.buy(ticket);

        assertThat(result).isEmpty();
    }

    @DisplayName("если сеанс не существует, сервис не пытается сохранить билет.")
    @Test
    void whenBuyTicketForAbsentSessionThenReturnEmptyOptional() {
        var context = createContext();
        var ticket = new Ticket(100, 2, 3, 1);
        when(context.filmSessionRepository.findById(100)).thenReturn(Optional.empty());

        var result = context.service.buy(ticket);

        assertThat(result).isEmpty();
        verifyNoInteractions(context.hallRepository, context.userRepository, context.ticketRepository);
    }

    @DisplayName("сервис собирает TicketDto для страницы успешной покупки.")
    @Test
    void whenFindByIdThenReturnTicketDto() {
        var context = createContext();
        var ticket = new Ticket(1, 1, 2, 3, 1);
        var film = new Film("Матрица", "Описание", 1999, 1, 16, 136, 1);
        film.setId(1);
        when(context.ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(context.filmSessionRepository.findById(1)).thenReturn(Optional.of(context.session));
        when(context.filmRepository.findById(1)).thenReturn(Optional.of(film));
        when(context.hallRepository.findById(1)).thenReturn(Optional.of(context.hall));
        when(context.userRepository.findById(1)).thenReturn(Optional.of(context.user));

        var result = context.service.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getFilmName()).isEqualTo("Матрица");
        assertThat(result.get().getUserFullName()).isEqualTo("Иван Иванов");
    }

    private TestContext createContext() {
        var ticketRepository = mock(TicketRepository.class);
        var filmSessionRepository = mock(FilmSessionRepository.class);
        var filmRepository = mock(FilmRepository.class);
        var hallRepository = mock(HallRepository.class);
        var userRepository = mock(UserRepository.class);
        var service = new SimpleTicketService(ticketRepository, filmSessionRepository,
                filmRepository, hallRepository, userRepository);
        return new TestContext(service, ticketRepository, filmSessionRepository,
                filmRepository, hallRepository, userRepository);
    }

    private void prepareValidPurchase(TestContext context, Ticket ticket) {
        when(context.filmSessionRepository.findById(ticket.getSessionId())).thenReturn(Optional.of(context.session));
        when(context.hallRepository.findById(context.session.getHallId())).thenReturn(Optional.of(context.hall));
        when(context.userRepository.findById(ticket.getUserId())).thenReturn(Optional.of(context.user));
    }

    private static class TestContext {
        private final SimpleTicketService service;
        private final TicketRepository ticketRepository;
        private final FilmSessionRepository filmSessionRepository;
        private final FilmRepository filmRepository;
        private final HallRepository hallRepository;
        private final UserRepository userRepository;
        private final FilmSession session = new FilmSession(1, 1, 1,
                LocalDateTime.of(2026, 5, 20, 10, 0),
                LocalDateTime.of(2026, 5, 20, 12, 16), 350);
        private final Hall hall = new Hall(1, "Красный зал", 6, 8, "Описание");
        private final User user = new User(1, "Иван Иванов", "ivan@example.com", "password");

        TestContext(SimpleTicketService service, TicketRepository ticketRepository,
                    FilmSessionRepository filmSessionRepository, FilmRepository filmRepository,
                    HallRepository hallRepository, UserRepository userRepository) {
            this.service = service;
            this.ticketRepository = ticketRepository;
            this.filmSessionRepository = filmSessionRepository;
            this.filmRepository = filmRepository;
            this.hallRepository = hallRepository;
            this.userRepository = userRepository;
        }
    }
}
