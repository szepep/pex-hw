package com.szepe.peter.pex;

import com.szepe.peter.pex.api.ImageReader;
import com.szepe.peter.pex.api.Try;
import com.szepe.peter.pex.impl.FileReader;
import com.szepe.peter.pex.spi.InputReader;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InputReader.InputReaderException {
        new Main().process();
    }

    void process() throws InputReader.InputReaderException {
        String path = "./test_data/input.txt";
        int top = 3;
        int readParallelism = 2;
        int processParallelism = 2;
        int queueSize = 10;

        InputReader reader = new FileReader(path);
        Stream<String> lines = reader.get();
        ImageReader imageReader = new ImageReader();

        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                processParallelism,
                processParallelism,
                0,
                TimeUnit.SECONDS,
                queue);

        ForkJoinPool readThreadPool = new ForkJoinPool(readParallelism);
        ForkJoinTask<?> readTasks = readThreadPool.submit(() -> {
            lines.parallel().forEach(
                    url -> Try.tryIt(imageReader::read).apply(url)
                            .consumeResult(image -> execute(executor, url, image, top))
                            .consumeException(p -> logger.warning("Unable to read image " + p.getValue()))
            );
        });

        readTasks.join();
        executor.shutdown();

    }

    private void execute(Executor executor, String url, BufferedImage image, int k) {
            logger.fine("Scheduling task " + url);
            executor.execute(ImageProcessingTask.of(url, image, k));
    }


}
