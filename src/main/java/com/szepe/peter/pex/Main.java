package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.spi.InputReader;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InputReader.InputReaderException {
        new Main().process();
    }

    void process() throws InputReader.InputReaderException {
        InputReader reader = new FileReader("./test_data/short.txt");
        reader.get().forEach(url -> {
            try {
                new ImageProcessorEngine(url).process();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
