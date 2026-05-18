package ru.job4j.cinema.dto;

import java.util.Arrays;

public class FileDto {
    private final String name;
    private final byte[] content;

    public FileDto(String name, byte[] content) {
        this.name = name;
        this.content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
    }

    public String getName() {
        return name;
    }

    public String name() {
        return getName();
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public byte[] content() {
        return getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileDto fileDto = (FileDto) o;
        return name.equals(fileDto.name)
                && Arrays.equals(content, fileDto.content);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
