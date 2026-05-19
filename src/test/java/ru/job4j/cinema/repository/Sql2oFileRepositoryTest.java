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
class Sql2oFileRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @DisplayName("файл находится по id с данными из Liquibase DML-скрипта.")
    @Test
    void whenFindByIdThenReturnSeededFile() {
        var file = fileRepository.findById(1);

        assertThat(file).isPresent();
        assertThat(file.get().getName()).isEqualTo("matrix.svg");
        assertThat(file.get().getPath()).isEqualTo("files/matrix.svg");
    }

    @DisplayName("если файла нет, репозиторий возвращает Optional.empty().")
    @Test
    void whenFindAbsentFileByIdThenReturnEmptyOptional() {
        var file = fileRepository.findById(100);

        assertThat(file).isEmpty();
    }
}
