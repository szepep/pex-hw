package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.spi.InputReader;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws InputReader.InputReaderException {
        new Main().process();
    }

    void process() throws InputReader.InputReaderException {
        InputReader reader = new FileReader("./test_data/short.txt");
        reader.get().forEach(url -> {
            try {
                List<ImageProcessorEngine.Pair<Color, Long>> process = new ImageProcessorEngine(url, 3).process();
                System.out.println(process);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
