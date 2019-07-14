package com.szepe.peter.pex.impl;

import com.szepe.peter.pex.spi.InputReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
class FileReader implements InputReader {

    private final String path;

    FileReader(@Value("${input}") String path) {
        this.path = path;
    }

    @Override
    public Stream<String> get() throws InputReaderException {
        try {
            return Files.lines(Paths.get(path));
        } catch (IOException e) {
            throw new InputReaderException("Unable to read file", e);
        }
    }
}
