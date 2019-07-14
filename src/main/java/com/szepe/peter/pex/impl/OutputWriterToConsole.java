package com.szepe.peter.pex.impl;

import com.szepe.peter.pex.spi.OutputWriter;

public class OutputWriterToConsole implements OutputWriter {
    @Override
    public void write(String s) {
        System.out.println(s);
    }

    @Override
    public void close() {
    }
}
