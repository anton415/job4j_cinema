package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.job4j.cinema.filter.SessionUser;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.user.UserService;

@ThreadSafe
@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("userToRegister", new User());
        return "users/register";
    }

    @PostMapping("/users/register")
    public String register(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var savedUser = userService.register(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с такой почтой уже существует");
            model.addAttribute("userToRegister", user);
            return "users/register";
        }
        SessionUser.set(request.getSession(), savedUser.get());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        Model model, HttpServletRequest request) {
        var user = userService.login(email, password);
        if (user.isEmpty()) {
            model.addAttribute("message", "Почта или пароль введены неверно");
            return "users/login";
        }
        SessionUser.set(request.getSession(), user.get());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
