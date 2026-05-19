package ru.job4j.cinema.repository.user;

import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.springframework.stereotype.Repository;

import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.common.Sql2oExceptionHelper;

@ThreadSafe
@Repository
public class Sql2oUserRepository implements UserRepository {
    private static final String SAVE = """
            INSERT INTO users (full_name, email, password)
            VALUES (:fullName, :email, :password)
            """;
    private static final String FIND_BY_EMAIL_AND_PASSWORD = """
            SELECT id,
                   full_name AS fullName,
                   email,
                   password
            FROM users
            WHERE email = :email AND password = :password
            """;
    private static final String FIND_BY_ID = """
            SELECT id,
                   full_name AS fullName,
                   email,
                   password
            FROM users
            WHERE id = :id
            """;

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.beginTransaction()) {
            var id = (Number) connection.createQuery(SAVE, true)
                    .bind(user)
                    .executeUpdate()
                    .getKey();
            user.setId(id.intValue());
            connection.commit();
            return Optional.of(user);
        } catch (Sql2oException exception) {
            if (Sql2oExceptionHelper.isUniqueViolation(exception)) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try (var connection = sql2o.open()) {
            var user = connection.createQuery(FIND_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var user = connection.createQuery(FIND_BY_EMAIL_AND_PASSWORD)
                    .addParameter("email", email)
                    .addParameter("password", password)
                    .executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
