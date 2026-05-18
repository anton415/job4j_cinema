package ru.job4j.cinema.repository;

import java.sql.SQLException;

final class Sql2oExceptionHelper {
    private static final String UNIQUE_VIOLATION = "23505";

    private Sql2oExceptionHelper() {
    }

    static boolean isUniqueViolation(Throwable throwable) {
        var current = throwable;
        while (current != null) {
            if (current instanceof SQLException sqlException
                    && UNIQUE_VIOLATION.equals(sqlException.getSQLState())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
