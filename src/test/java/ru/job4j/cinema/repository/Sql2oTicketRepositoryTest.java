package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class Sql2oTicketRepositoryTest {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sql2oTestHelper sql2oTestHelper;

    @BeforeEach
    void setUp() {
        sql2oTestHelper.clearMutableTables();
    }

    /**
     * Сценарий: билет сохраняется, находится по id и входит в список билетов сеанса.
     */
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

    /**
     * Сценарий: повторная покупка того же места на том же сеансе возвращает Optional.empty().
     */
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
