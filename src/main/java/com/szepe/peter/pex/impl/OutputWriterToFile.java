package com.szepe.peter.pex.impl;

import com.szepe.peter.pex.rx.ByteArrayToBufferedImageOperatorProvider;
import com.szepe.peter.pex.spi.OutputWriter;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class OutputWriterToFile implements OutputWriter {

    private final static Logger logger = Logger.getLogger(ByteArrayToBufferedImageOperatorProvider.class.getName());

    private final FileWriter writer;
    private final String newLine = System.getProperty("line.separator");
    private final AtomicInteger counter = new AtomicInteger();


    public OutputWriterToFile(@Value("${output}") String output) throws IOException {
        writer = new FileWriter(output);
    }

    @Override
    public void write(String s) throws OutputWriterException {
        try {
            writer.write(s);
            writer.write(newLine);
            if (counter.incrementAndGet() % 10 == 0) {
                writer.flush();
            }
        } catch (IOException e) {
            throw new OutputWriterException("Unable to write to file", e);
        }
    }

    @Override
    public void close() throws OutputWriterException {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new OutputWriterException("Problem while closing file", e);
        }

    }
}
