package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class IndexControllerTest {

    /**
     * Сценарий: главная страница доступна по корневому URL.
     */
    @Test
    void whenGetRootThenReturnIndexView() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new IndexController())
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    /**
     * Сценарий: главная страница доступна по /index.
     */
    @Test
    void whenGetIndexThenReturnIndexView() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new IndexController())
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
