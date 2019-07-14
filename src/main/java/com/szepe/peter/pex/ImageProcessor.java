package com.szepe.peter.pex;

import com.szepe.peter.pex.rx.BufferedImageToTopKOperatorProvider;
import com.szepe.peter.pex.rx.ByteArrayToBufferedImageOperatorProvider;
import com.szepe.peter.pex.rx.ConvertToResultFormatOperatorProvider;
import com.szepe.peter.pex.rx.DownloadImageAsByteArrayOperatorProvider;
import com.szepe.peter.pex.spi.InputReader;
import com.szepe.peter.pex.spi.OutputWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
class ImageProcessor {

    private final static Logger logger = Logger.getLogger(ImageProcessor.class.getName());

    private final InputReader inputReader;
    private final DownloadImageAsByteArrayOperatorProvider downloadImageAsByteArrayOperatorProvider;
    private final ByteArrayToBufferedImageOperatorProvider byteArrayToBufferedImageOperatorProvider;
    private final BufferedImageToTopKOperatorProvider bufferedImageToTopKOperatorProvider;
    private final ConvertToResultFormatOperatorProvider convertToResultFormatOperatorProvider;
    private final OutputWriter outputWriter;
    private final int downloadThreads;
    private final int computationThreads;

    @Autowired
    public ImageProcessor(InputReader inputReader,
                          DownloadImageAsByteArrayOperatorProvider downloadImageAsByteArrayOperatorProvider,
                          ByteArrayToBufferedImageOperatorProvider byteArrayToBufferedImageOperatorProvider,
                          BufferedImageToTopKOperatorProvider bufferedImageToTopKOperatorProvider,
                          ConvertToResultFormatOperatorProvider convertToResultFormatOperatorProvider,
                          OutputWriter outputWriter,
                          @Value("${thread_count.download}") int downloadThreads,
                          @Value("${thread_count.computation}") int computationThreads) {
        this.inputReader = inputReader;
        this.downloadImageAsByteArrayOperatorProvider = downloadImageAsByteArrayOperatorProvider;
        this.byteArrayToBufferedImageOperatorProvider = byteArrayToBufferedImageOperatorProvider;
        this.bufferedImageToTopKOperatorProvider = bufferedImageToTopKOperatorProvider;
        this.convertToResultFormatOperatorProvider = convertToResultFormatOperatorProvider;
        this.outputWriter = outputWriter;
        this.downloadThreads = downloadThreads;
        this.computationThreads = computationThreads;
    }

    void process() throws InputReader.InputReaderException {
        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("Number of processors: " + processors);
        long maxMemory = Runtime.getRuntime().maxMemory();
        logger.info("Max memory: " + maxMemory);

        ExecutorService downloadExecutor = Executors.newFixedThreadPool(downloadThreads);
        Scheduler downloadScheduler = Schedulers.from(downloadExecutor);
        ExecutorService computationExecutor = Executors.newFixedThreadPool(computationThreads);
        Scheduler computationScheduler = Schedulers.from(computationExecutor);

        Observable.from(inputReader.get()::iterator)
                .flatMap(url -> Observable.just(url)
                                .observeOn(downloadScheduler)
                                .lift(downloadImageAsByteArrayOperatorProvider.get()),
                        downloadThreads)
                .flatMap(byteArray -> Observable.just(byteArray)
                                .observeOn(computationScheduler)
                                .lift(byteArrayToBufferedImageOperatorProvider.get())
                                .lift(bufferedImageToTopKOperatorProvider.get())
                                .lift(convertToResultFormatOperatorProvider.get())
                        , computationThreads)
                .toBlocking()
                .subscribe(outputWriter::write);

        downloadExecutor.shutdown();
        computationExecutor.shutdown();
    }

}
