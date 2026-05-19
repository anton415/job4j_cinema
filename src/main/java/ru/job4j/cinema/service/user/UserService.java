package ru.job4j.cinema.service.user;

import java.util.Optional;

import ru.job4j.cinema.model.User;

public interface UserService {
    Optional<User> register(User user);

    Optional<User> login(String email, String password);

    Optional<User> findById(int id);
}
