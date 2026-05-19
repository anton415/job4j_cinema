package ru.job4j.cinema.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.film.FilmService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class FilmControllerTest {

    @DisplayName("страница кинотеки получает список фильмов из сервиса.")
    @Test
    void whenGetFilmsThenReturnFilmsListView() throws Exception {
        var filmService = mock(FilmService.class);
        when(filmService.findAll()).thenReturn(List.of(new FilmDto()));
        var mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(filmService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(view().name("films/list"))
                .andExpect(model().attributeExists("films"));
    }
}
