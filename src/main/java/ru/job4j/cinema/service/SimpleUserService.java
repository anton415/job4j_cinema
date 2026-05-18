package ru.job4j.cinema.service;

import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.springframework.stereotype.Service;

import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;

@ThreadSafe
@Service
public class SimpleUserService implements UserService {
    private final UserRepository userRepository;

    public SimpleUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> register(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }
}
