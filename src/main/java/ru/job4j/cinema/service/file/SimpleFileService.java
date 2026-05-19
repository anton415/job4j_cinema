package ru.job4j.cinema.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.FileRepository;

@ThreadSafe
@Service
public class SimpleFileService implements FileService {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleFileService.class);

    private final FileRepository fileRepository;

    public SimpleFileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public Optional<FileDto> getFileById(int id) {
        return fileRepository.findById(id).flatMap(this::readFile);
    }

    private Optional<FileDto> readFile(File file) {
        try {
            return Optional.of(new FileDto(file.getName(), Files.readAllBytes(Path.of(file.getPath()))));
        } catch (IOException exception) {
            LOG.error("Failed to read file: id={}, name={}, path={}",
                    file.getId(), file.getName(), file.getPath(), exception);
            return Optional.empty();
        }
    }
}
