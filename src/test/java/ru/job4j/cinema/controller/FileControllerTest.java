package ru.job4j.cinema.controller;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.file.FileService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest {

    @DisplayName("постер отдается байтами через /files/{id}.")
    @Test
    void whenGetExistingFileThenReturnBytes() throws Exception {
        var fileService = mock(FileService.class);
        when(fileService.getFileById(1))
                .thenReturn(Optional.of(new FileDto("poster.svg", "<svg></svg>".getBytes())));
        var mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService)).build();

        mockMvc.perform(get("/files/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/svg+xml"))
                .andExpect(content().bytes("<svg></svg>".getBytes()));
    }

    @DisplayName("если файл не найден, контроллер возвращает 404.")
    @Test
    void whenGetAbsentFileThenReturnNotFound() throws Exception {
        var fileService = mock(FileService.class);
        when(fileService.getFileById(1)).thenReturn(Optional.empty());
        var mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService)).build();

        mockMvc.perform(get("/files/1"))
                .andExpect(status().isNotFound());
    }
}
