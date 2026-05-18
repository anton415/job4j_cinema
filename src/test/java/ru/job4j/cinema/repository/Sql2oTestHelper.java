package ru.job4j.cinema.repository;

import org.sql2o.Sql2o;
import org.springframework.stereotype.Component;

@Component
public class Sql2oTestHelper {
    private static final String DELETE_TICKETS = "DELETE FROM tickets";
    private static final String DELETE_USERS = "DELETE FROM users";
    private static final String RESTART_TICKETS_ID = "ALTER SEQUENCE tickets_id_seq RESTART WITH 1";
    private static final String RESTART_USERS_ID = "ALTER SEQUENCE users_id_seq RESTART WITH 1";

    private final Sql2o sql2o;

    public Sql2oTestHelper(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void clearMutableTables() {
        try (var connection = sql2o.open()) {
            connection.createQuery(DELETE_TICKETS).executeUpdate();
            connection.createQuery(DELETE_USERS).executeUpdate();
            connection.createQuery(RESTART_TICKETS_ID).executeUpdate();
            connection.createQuery(RESTART_USERS_ID).executeUpdate();
        }
    }
}
