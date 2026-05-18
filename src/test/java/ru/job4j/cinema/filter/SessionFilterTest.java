package ru.job4j.cinema.filter;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import ru.job4j.cinema.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionFilterTest {

    /**
     * Сценарий: для гостя фильтр добавляет гостевого пользователя в request attribute.
     */
    @Test
    void whenGuestRequestsPageThenRequestContainsGuestUser() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilters(new SessionFilter())
                .build();

        mockMvc.perform(get("/user-name"))
                .andExpect(status().isOk())
                .andExpect(content().string("Гость"));
    }

    /**
     * Сценарий: для авторизованного пользователя фильтр добавляет session user в request.
     */
    @Test
    void whenAuthorizedUserRequestsPageThenRequestContainsSessionUser() throws Exception {
        var session = new MockHttpSession();
        SessionUser.set(session, new User(1, "Иван Иванов", "ivan@example.com", "password"));
        var mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilters(new SessionFilter())
                .build();

        mockMvc.perform(get("/user-name").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Иван Иванов"));
    }

    @Controller
    static class TestController {
        @GetMapping("/user-name")
        ResponseEntity<String> userName(HttpServletRequest request) {
            var user = (User) request.getAttribute(SessionUser.ATTRIBUTE);
            return ResponseEntity.ok()
                    .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body(user.getFullName());
        }
    }
}
