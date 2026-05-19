package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.ticket.Sql2oTicketRepository;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.repository.user.Sql2oUserRepository;
import ru.job4j.cinema.repository.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oTicketRepositoryTest {
    private TicketRepository ticketRepository;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        var sql2o = Sql2oTestHelper.initSql2o();
        ticketRepository = new Sql2oTicketRepository(sql2o);
        userRepository = new Sql2oUserRepository(sql2o);
    }

    @DisplayName("билет сохраняется, находится по id и входит в список билетов сеанса.")
    @Test
    void whenSaveTicketThenCanFindByIdAndSessionId() {
        var user = userRepository.save(new User("Иван Иванов", "ivan@example.com", "password")).get();
        var ticket = new Ticket(1, 2, 3, user.getId());

        var saved = ticketRepository.save(ticket);
        var found = ticketRepository.findById(1);
        var sessionTickets = ticketRepository.findBySessionId(1);

        assertThat(saved).isPresent();
        assertThat(saved.get().getId()).isEqualTo(1);
        assertThat(found).contains(saved.get());
        assertThat(sessionTickets).containsExactly(saved.get());
    }

    @DisplayName("повторная покупка того же места на том же сеансе возвращает Optional.empty().")
    @Test
    void whenSaveTicketWithSameSessionRowAndPlaceThenReturnEmptyOptional() {
        var user = userRepository.save(new User("Иван Иванов", "ivan@example.com", "password")).get();
        var first = new Ticket(1, 2, 3, user.getId());
        var second = new Ticket(1, 2, 3, user.getId());

        var saved = ticketRepository.save(first);
        var duplicate = ticketRepository.save(second);

        assertThat(saved).isPresent();
        assertThat(duplicate).isEmpty();
    }
}
