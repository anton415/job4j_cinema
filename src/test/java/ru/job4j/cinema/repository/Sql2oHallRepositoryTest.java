package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oHallRepositoryTest {
    private HallRepository hallRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        hallRepository = new Sql2oHallRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("зал находится по id с корректным маппингом SQL alias в Java-поля.")
    @Test
    void whenFindByIdThenReturnHall() {
        var hall = hallRepository.findById(1);

        assertThat(hall).isPresent();
        assertThat(hall.get().getName()).isEqualTo("Красный зал");
        assertThat(hall.get().getRowCount()).isEqualTo(6);
        assertThat(hall.get().getPlaceCount()).isEqualTo(8);
    }

    @DisplayName("если зала нет, репозиторий возвращает Optional.empty().")
    @Test
    void whenFindAbsentHallByIdThenReturnEmptyOptional() {
        var hall = hallRepository.findById(100);

        assertThat(hall).isEmpty();
    }
}
