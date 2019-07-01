package com.szepe.peter.pex;

import com.szepe.peter.pex.api.ImageReader;
import com.szepe.peter.pex.api.Try;
import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.spi.InputReader;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Use -XX:ActiveProcessorCount=1 to set CPU count to 1
 */
@Ignore
public class DownloadPerformaceTest {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    @Test
    public void performanceTest() {

        int processors = Runtime.getRuntime().availableProcessors();
        logger.info("Number of processors: " + processors);

        long pool1 = tryToReadImages(1);
        long pool2 = tryToReadImages(2);
        long pool4 = tryToReadImages(4);
        long pool6 = tryToReadImages(6);

        logger.info("1 thread time" + pool1);
        logger.info("2 thread time" + pool2);
        logger.info("4 thread time" + pool4);
        logger.info("6 thread time " + pool6);
    }

    private long tryToReadImages(int parallelism) {
        long startTime = System.nanoTime();
        try {
            InputReader reader = new FileReader("./test_data/input.txt");
            Stream<String> lines = reader.get();
            ImageReader imageReader = new ImageReader();
            ForkJoinPool customThreadPool = new ForkJoinPool(parallelism);
            final AtomicInteger cnt = new AtomicInteger();
            customThreadPool.submit(() -> {
                lines.parallel().forEach(
                        url -> Try.tryIt(imageReader::read).apply(url)
                                .consumeResult(image -> printProgress(cnt))
                                .consumeException(p -> printProgress(cnt))
                );
            }).get();
        } catch (InputReader.InputReaderException e) {
            logger.info("Unable to read file");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long elapsedTime = System.nanoTime() - startTime;
        logger.info(parallelism + " thread time: " + elapsedTime);
        return elapsedTime;
    }

    private void printProgress(AtomicInteger cnt) {
        int i = cnt.incrementAndGet();
        if (i % 50 == 0) {
            logger.info("Progress " + i);
        }
    }

}
