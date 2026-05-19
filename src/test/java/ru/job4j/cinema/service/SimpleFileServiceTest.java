package ru.job4j.cinema.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFileServiceTest {
    @TempDir
    private Path tempDir;

    @DisplayName("сервис читает существующий файл по пути из репозитория.")
    @Test
    void whenGetExistingFileThenReturnFileDto() throws Exception {
        var path = tempDir.resolve("poster.svg");
        var content = "<svg></svg>".getBytes();
        Files.write(path, content);
        var fileRepository = mock(FileRepository.class);
        when(fileRepository.findById(1)).thenReturn(Optional.of(new File(1, "poster.svg", path.toString())));
        var fileService = new SimpleFileService(fileRepository);

        var result = fileService.getFileById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("poster.svg");
        assertThat(result.get().getContent()).containsExactly(content);
    }

    @DisplayName("если файл отсутствует в хранилище, сервис возвращает Optional.empty().")
    @Test
    void whenFileContentDoesNotExistThenReturnEmptyOptional() {
        var fileRepository = mock(FileRepository.class);
        when(fileRepository.findById(1)).thenReturn(Optional.of(new File(1, "poster.svg", "missing.svg")));
        var fileService = new SimpleFileService(fileRepository);

        var result = fileService.getFileById(1);

        assertThat(result).isEmpty();
    }
}
