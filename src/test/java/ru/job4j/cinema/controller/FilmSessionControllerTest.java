package ru.job4j.cinema.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.filmsession.FilmSessionService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class FilmSessionControllerTest {

    @DisplayName("страница расписания получает список сеансов из сервиса.")
    @Test
    void whenGetSessionsThenReturnSessionsListView() throws Exception {
        var filmSessionService = mock(FilmSessionService.class);
        when(filmSessionService.findAll()).thenReturn(List.of(new FilmSessionDto()));
        var mockMvc = MockMvcBuilders.standaloneSetup(new FilmSessionController(filmSessionService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/sessions"))
                .andExpect(status().isOk())
                .andExpect(view().name("sessions/list"))
                .andExpect(model().attributeExists("sessions"));
    }
}
