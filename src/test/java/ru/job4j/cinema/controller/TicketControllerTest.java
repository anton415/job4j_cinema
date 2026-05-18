package ru.job4j.cinema.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.filter.SessionUser;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.TicketService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class TicketControllerTest {

    /**
     * Сценарий: форма покупки получает сеанс, фильм, ряды и места.
     */
    @Test
    void whenGetBuyPageThenReturnBuyView() throws Exception {
        var context = createContext();
        var session = createFilmSessionDto();
        when(context.filmSessionService.findById(1)).thenReturn(Optional.of(session));
        when(context.filmSessionService.findRowsBySessionId(1)).thenReturn(List.of(1, 2));
        when(context.filmSessionService.findPlacesBySessionId(1)).thenReturn(List.of(1, 2, 3));
        when(context.filmService.findById(1)).thenReturn(Optional.of(new FilmDto()));

        context.mockMvc.perform(get("/tickets/buy/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/buy"))
                .andExpect(model().attributeExists("session", "film", "rows", "places", "ticket"));
    }

    /**
     * Сценарий: гость при отправке покупки перенаправляется на страницу входа.
     */
    @Test
    void whenGuestBuysTicketThenRedirectToLogin() throws Exception {
        var context = createContext();

        context.mockMvc.perform(post("/tickets")
                        .param("sessionId", "1")
                        .param("rowNumber", "1")
                        .param("placeNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Сценарий: авторизованный пользователь после покупки попадает на страницу успеха.
     */
    @Test
    void whenAuthorizedUserBuysTicketThenRedirectToSuccessPage() throws Exception {
        var context = createContext();
        var session = new MockHttpSession();
        SessionUser.set(session, new User(1, "Иван Иванов", "ivan@example.com", "password"));
        when(context.ticketService.buy(any(Ticket.class))).thenReturn(Optional.of(new Ticket(5, 1, 1, 1, 1)));

        context.mockMvc.perform(post("/tickets").session(session)
                        .param("sessionId", "1")
                        .param("rowNumber", "1")
                        .param("placeNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets/success/5"));
    }

    /**
     * Сценарий: занятое место перенаправляет на страницу неудачной покупки.
     */
    @Test
    void whenPlaceOccupiedThenRedirectToFailurePage() throws Exception {
        var context = createContext();
        var session = new MockHttpSession();
        SessionUser.set(session, new User(1, "Иван Иванов", "ivan@example.com", "password"));
        when(context.ticketService.buy(any(Ticket.class))).thenReturn(Optional.empty());

        context.mockMvc.perform(post("/tickets").session(session)
                        .param("sessionId", "1")
                        .param("rowNumber", "1")
                        .param("placeNumber", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets/failure/1"));
    }

    /**
     * Сценарий: страница успешной покупки получает TicketDto.
     */
    @Test
    void whenGetSuccessPageThenReturnSuccessView() throws Exception {
        var context = createContext();
        when(context.ticketService.findById(5)).thenReturn(Optional.of(new TicketDto()));

        context.mockMvc.perform(get("/tickets/success/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/success"))
                .andExpect(model().attributeExists("ticket"));
    }

    /**
     * Сценарий: страница неудачной покупки сохраняет id сеанса для повторной попытки.
     */
    @Test
    void whenGetFailurePageThenReturnFailureView() throws Exception {
        var context = createContext();

        context.mockMvc.perform(get("/tickets/failure/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets/failure"))
                .andExpect(model().attribute("sessionId", 1));
    }

    private TestContext createContext() {
        var ticketService = mock(TicketService.class);
        var filmSessionService = mock(FilmSessionService.class);
        var filmService = mock(FilmService.class);
        var controller = new TicketController(ticketService, filmSessionService, filmService);
        var mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolver.noOp())
                .build();
        return new TestContext(mockMvc, ticketService, filmSessionService, filmService);
    }

    private FilmSessionDto createFilmSessionDto() {
        var session = new FilmSessionDto();
        session.setId(1);
        session.setFilmId(1);
        return session;
    }

    private static class TestContext {
        private final org.springframework.test.web.servlet.MockMvc mockMvc;
        private final TicketService ticketService;
        private final FilmSessionService filmSessionService;
        private final FilmService filmService;

        TestContext(org.springframework.test.web.servlet.MockMvc mockMvc, TicketService ticketService,
                    FilmSessionService filmSessionService, FilmService filmService) {
            this.mockMvc = mockMvc;
            this.ticketService = ticketService;
            this.filmSessionService = filmSessionService;
            this.filmService = filmService;
        }
    }
}
