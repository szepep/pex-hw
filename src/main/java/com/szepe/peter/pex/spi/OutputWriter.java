package com.szepe.peter.pex.spi;

public interface OutputWriter {
    void write(String s) throws OutputWriterException;

    void close() throws OutputWriterException;

    class OutputWriterException extends Exception {
        public OutputWriterException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
