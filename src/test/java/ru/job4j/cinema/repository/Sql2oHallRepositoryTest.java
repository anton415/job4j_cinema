package ru.job4j.cinema.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Tag("testcontainers")
@Testcontainers
@Import(PostgresContainerConfiguration.class)
class Sql2oHallRepositoryTest {
    @Autowired
    private HallRepository hallRepository;

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
