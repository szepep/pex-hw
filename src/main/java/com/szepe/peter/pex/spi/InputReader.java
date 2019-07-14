package com.szepe.peter.pex.spi;

import java.util.stream.Stream;

@FunctionalInterface
public interface InputReader {

    Stream<String> get() throws InputReaderException;

    class InputReaderException extends Exception {
        public InputReaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
