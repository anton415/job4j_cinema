package ru.job4j.cinema.repository;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.job4j.cinema.repository.file.FileRepository;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFileRepositoryTest {
    private FileRepository fileRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        fileRepository = new Sql2oFileRepository(Sql2oTestHelper.initSql2o());
    }

    @DisplayName("файл находится по id с данными из SQL DML-скрипта.")
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
