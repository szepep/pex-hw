package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.rx.BufferedImageToTopK;
import com.szepe.peter.pex.rx.ByteArrayToBufferedImage;
import com.szepe.peter.pex.rx.DownloadImageAsByteArray;
import com.szepe.peter.pex.spi.InputReader;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InputReader.InputReaderException {
        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("Number of processors: " + processors);
        long maxMemory = Runtime.getRuntime().maxMemory();
        logger.info("Max memory: " + maxMemory);
        new Main().rxProcess();
    }

    void rxProcess() throws InputReader.InputReaderException {
        String path = "./test_data/input.txt";

        FileReader fileReader = new FileReader(path);
        long startTime = System.nanoTime();
        Observable.from(fileReader.get()::iterator)
                .window(100)
                .flatMap(urls -> urls
                        .observeOn(Schedulers.io())
                        .lift(new DownloadImageAsByteArray())
                )
                .window(10)
                .flatMap(byteArrays -> byteArrays
                        .observeOn(Schedulers.computation())
                        .lift(new ByteArrayToBufferedImage())
                        .lift(new BufferedImageToTopK(3))
                )
                .toBlocking()
                .subscribe(next -> System.out.println(next.getFirst() + ": " + next.getSecond()),
                        error -> logger.log(Level.WARNING, "unable to download image", error));

        long elapsedTime = System.nanoTime() - startTime;
        logger.log(Level.INFO, "Computation took " + elapsedTime / 1_000_000);
    }

}
