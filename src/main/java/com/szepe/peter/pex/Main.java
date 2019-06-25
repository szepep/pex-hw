package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.spi.InputReader;

public class Main {

    public static void main(String[] args) throws InputReader.InputReaderException {
        new Main().process();
    }

    void process() throws InputReader.InputReaderException {
        InputReader reader = new FileReader("./test_data/short.txt");
        reader.get().forEach(System.out::println);
    }
}
