package com.szepe.peter.pex.spi;

import java.util.stream.Stream;

@FunctionalInterface
public interface InputReader {

    Stream<String> get() throws InputReaderException;

    class InputReaderException extends Exception {
        public InputReaderException() {
        }

        public InputReaderException(String message) {
            super(message);
        }

        public InputReaderException(String message, Throwable cause) {
            super(message, cause);
        }

        public InputReaderException(Throwable cause) {
            super(cause);
        }

        public InputReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
