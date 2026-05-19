package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import ru.job4j.cinema.filter.SessionUser;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.filmsession.FilmSessionService;
import ru.job4j.cinema.service.ticket.TicketService;

@ThreadSafe
@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final FilmSessionService filmSessionService;
    private final FilmService filmService;

    public TicketController(TicketService ticketService, FilmSessionService filmSessionService,
                            FilmService filmService) {
        this.ticketService = ticketService;
        this.filmSessionService = filmSessionService;
        this.filmService = filmService;
    }

    @GetMapping("/buy/{sessionId}")
    public String getBuyPage(@PathVariable int sessionId, Model model) {
        var session = filmSessionService.findById(sessionId);
        if (session.isEmpty()) {
            return addNotFound(model, "Сеанс с указанным идентификатором не найден");
        }
        model.addAttribute("filmSession", session.get());
        model.addAttribute("film", filmService.findById(session.get().getFilmId()).orElse(null));
        model.addAttribute("rows", filmSessionService.findRowsBySessionId(sessionId));
        model.addAttribute("places", filmSessionService.findPlacesBySessionId(sessionId));
        model.addAttribute("ticket", new Ticket(sessionId, 0, 0, 0));
        return "tickets/buy";
    }

    @PostMapping
    public String buy(@ModelAttribute Ticket ticket,
                      @SessionAttribute(SessionUser.ATTRIBUTE) User user) {
        ticket.setUserId(user.getId());
        return ticketService.buy(ticket)
                .map(saved -> "redirect:/tickets/success/" + saved.getId())
                .orElse("redirect:/tickets/failure/" + ticket.getSessionId());
    }

    @GetMapping("/success/{ticketId}")
    public String getSuccessPage(@PathVariable int ticketId, Model model) {
        var ticket = ticketService.findById(ticketId);
        if (ticket.isEmpty()) {
            return addNotFound(model, "Билет с указанным идентификатором не найден");
        }
        model.addAttribute("ticket", ticket.get());
        return "tickets/success";
    }

    @GetMapping("/failure/{sessionId}")
    public String getFailurePage(@PathVariable int sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "tickets/failure";
    }

    private String addNotFound(Model model, String message) {
        model.addAttribute("message", message);
        return "errors/404";
    }
}
