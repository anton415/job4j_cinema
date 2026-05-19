package ru.job4j.cinema.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ru.job4j.cinema.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizationFilterTest {

    @DisplayName("публичная страница доступна гостю.")
    @Test
    void whenGuestRequestsPublicPageThenRequestPasses() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilters(new AuthorizationFilter())
                .build();

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @DisplayName("гость при покупке билета перенаправляется на /login.")
    @Test
    void whenGuestPostsTicketThenRedirectToLogin() throws Exception {
        var mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilters(new AuthorizationFilter())
                .build();

        mockMvc.perform(post("/tickets"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @DisplayName("авторизованный пользователь проходит к защищенному действию.")
    @Test
    void whenAuthorizedUserRequestsProtectedPageThenRequestPasses() throws Exception {
        var session = new MockHttpSession();
        SessionUser.set(session, new User(1, "Иван Иванов", "ivan@example.com", "password"));
        var mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilters(new AuthorizationFilter())
                .build();

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().isOk());
    }

    @Controller
    static class TestController {
        @GetMapping("/films")
        ResponseEntity<String> films() {
            return ResponseEntity.ok("films");
        }

        @PostMapping("/tickets")
        ResponseEntity<String> tickets() {
            return ResponseEntity.ok("tickets");
        }

        @GetMapping("/logout")
        ResponseEntity<String> logout() {
            return ResponseEntity.ok("logout");
        }
    }
}
