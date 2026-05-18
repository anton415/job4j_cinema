package ru.job4j.cinema.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.job4j.cinema.filter.SessionUser;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class UserControllerTest {

    /**
     * Сценарий: страница регистрации возвращает форму.
     */
    @Test
    void whenGetRegisterPageThenReturnRegisterView() throws Exception {
        var userService = mock(UserService.class);
        var mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeExists("userToRegister"));
    }

    /**
     * Сценарий: успешная регистрация сохраняет пользователя в session и ведет на главную.
     */
    @Test
    void whenRegisterThenRedirectToRootAndStoreUserInSession() throws Exception {
        var userService = mock(UserService.class);
        var user = new User(1, "Иван Иванов", "ivan@example.com", "password");
        when(userService.register(any(User.class))).thenReturn(Optional.of(user));
        var mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Иван Иванов")
                        .param("email", "ivan@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionUser.ATTRIBUTE, user));
    }

    /**
     * Сценарий: при дублировании email пользователь остается на форме регистрации.
     */
    @Test
    void whenRegisterDuplicateEmailThenReturnRegisterViewWithMessage() throws Exception {
        var userService = mock(UserService.class);
        when(userService.register(any(User.class))).thenReturn(Optional.empty());
        var mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(post("/users/register")
                        .param("fullName", "Иван Иванов")
                        .param("email", "ivan@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"))
                .andExpect(model().attributeExists("message"));
    }

    /**
     * Сценарий: успешный вход сохраняет пользователя в session и ведет на главную.
     */
    @Test
    void whenLoginThenRedirectToRootAndStoreUserInSession() throws Exception {
        var userService = mock(UserService.class);
        var user = new User(1, "Иван Иванов", "ivan@example.com", "password");
        when(userService.login("ivan@example.com", "password")).thenReturn(Optional.of(user));
        var mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(post("/login")
                        .param("email", "ivan@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute(SessionUser.ATTRIBUTE, user));
    }

    /**
     * Сценарий: выход инвалидирует session и ведет на главную.
     */
    @Test
    void whenLogoutThenRedirectToRoot() throws Exception {
        var userService = mock(UserService.class);
        var session = new MockHttpSession();
        SessionUser.set(session, new User(1, "Иван Иванов", "ivan@example.com", "password"));
        var mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(TestViewResolver.noOp())
                .build();

        mockMvc.perform(get("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
