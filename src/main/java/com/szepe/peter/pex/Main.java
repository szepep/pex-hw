package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.BImageToTopKMap;
import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.rx.BufferedImageToTopKOperatorProvider;
import com.szepe.peter.pex.rx.ByteArrayToBufferedImage;
import com.szepe.peter.pex.rx.ConvertToResultFormatOperatorProvider;
import com.szepe.peter.pex.rx.DownloadImageAsByteArray;
import com.szepe.peter.pex.spi.BufferedImageToTopK;
import com.szepe.peter.pex.spi.InputReader;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InputReader.InputReaderException {
        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("Number of processors: " + processors);
        long maxMemory = Runtime.getRuntime().maxMemory();
        logger.info("Max memory: " + maxMemory);
        new Main().rxProcess();
    }

    private void rxProcess() throws InputReader.InputReaderException {
        String path = "./test_data/input.txt";

        FileReader fileReader = new FileReader(path);
        int downloadThreads = 10;
        int computationThreads = 1;

        ExecutorService downloadExecutor = Executors.newFixedThreadPool(downloadThreads);
        Scheduler downloadScheduler = Schedulers.from(downloadExecutor);
        ExecutorService computationExecutor = Executors.newFixedThreadPool(computationThreads);
        Scheduler computationScheduler = Schedulers.from(computationExecutor);

        BufferedImageToTopK logic = new BImageToTopKMap(3);

        Observable.from(fileReader.get()::iterator)
                .flatMap(url -> Observable.just(url)
                                .observeOn(downloadScheduler)
                                .lift(new DownloadImageAsByteArray().get()),
                        downloadThreads)
                .flatMap(byteArray -> Observable.just(byteArray)
                                .observeOn(computationScheduler)
                                .lift(new ByteArrayToBufferedImage().get())
                                .lift(new BufferedImageToTopKOperatorProvider(logic).get())
                                .lift(new ConvertToResultFormatOperatorProvider().get())
                        , computationThreads)
                .toBlocking()
                .subscribe(System.out::println);

        downloadExecutor.shutdown();
        computationExecutor.shutdown();
    }

}
