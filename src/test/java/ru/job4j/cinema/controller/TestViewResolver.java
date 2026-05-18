package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

final class TestViewResolver {
    private TestViewResolver() {
    }

    static ViewResolver noOp() {
        return (viewName, locale) -> {
            if (viewName.startsWith("redirect:")) {
                return new RedirectView(viewName.substring("redirect:".length()));
            }
            return (model, request, response) ->
                    response.setStatus(HttpServletResponse.SC_OK);
        };
    }
}
