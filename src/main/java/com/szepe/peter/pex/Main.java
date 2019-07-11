package com.szepe.peter.pex;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.rx.BufferedImageToTopKArray;
import com.szepe.peter.pex.rx.ByteArrayToBufferedImage;
import com.szepe.peter.pex.rx.DownloadImageAsByteArray;
import com.szepe.peter.pex.rx.Operator;
import com.szepe.peter.pex.spi.InputReader;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        int downloadThreads = 10;
        int computationThreads = 1;

        ExecutorService downloadExecutor = Executors.newFixedThreadPool(downloadThreads);
        Scheduler downloadScheduler = Schedulers.from(downloadExecutor);
        ExecutorService computationExecutor = Executors.newFixedThreadPool(computationThreads);
        Scheduler computationScheduler = Schedulers.from(computationExecutor);

        Operator<String, Pair<String, byte[]>, IOException> downloadImageOperator = new DownloadImageAsByteArray().get();
        Operator<Pair<String, byte[]>, Pair<String, BufferedImage>, IOException> readToBufferedImageOperator = new ByteArrayToBufferedImage().get();
        Operator<Pair<String, BufferedImage>, Pair<String, List<Pair<Integer, Integer>>>, Exception> topKColorOperator = new BufferedImageToTopKArray(3, computationThreads).get();

        Observable.from(fileReader.get()::iterator)
                .flatMap(url -> Observable.just(url)
                                .observeOn(downloadScheduler)
                                .lift(downloadImageOperator),
                        downloadThreads)
                .flatMap(byteArray -> Observable.just(byteArray)
                                .observeOn(computationScheduler)
                                .lift(readToBufferedImageOperator)
                                .lift(topKColorOperator),
                        computationThreads
                )
                .toBlocking()
                .subscribe(next -> System.out.println(next.getFirst() + ": " + next.getSecond()));

        logger.log(Level.INFO, downloadImageOperator.printStats());
        logger.log(Level.INFO, readToBufferedImageOperator.printStats());
        logger.log(Level.INFO, topKColorOperator.printStats());

        downloadExecutor.shutdown();
        computationExecutor.shutdown();
    }

}
