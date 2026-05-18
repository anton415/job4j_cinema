package ru.job4j.cinema.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import ru.job4j.cinema.model.User;

public final class SessionUser {
    public static final String ATTRIBUTE = "user";
    private static final User GUEST = new User(0, "Гость", "", "");

    private SessionUser() {
    }

    public static User get(HttpServletRequest request) {
        var user = request.getSession().getAttribute(ATTRIBUTE);
        if (user instanceof User sessionUser) {
            return sessionUser;
        }
        return GUEST;
    }

    public static void set(HttpSession session, User user) {
        session.setAttribute(ATTRIBUTE, user);
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(ATTRIBUTE);
    }

    public static boolean isAuthorized(HttpServletRequest request) {
        return get(request).getId() > 0;
    }

    public static User guest() {
        return GUEST;
    }
}
