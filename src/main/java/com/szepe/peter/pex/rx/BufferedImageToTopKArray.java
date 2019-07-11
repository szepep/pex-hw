package com.szepe.peter.pex.rx;

import com.google.common.collect.Comparators;
import com.google.common.collect.Streams;
import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BufferedImageToTopKArray implements OperatorProvider<Pair<String, BufferedImage>, Pair<String, List<Pair<Color, Integer>>>, Exception> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopKArray.class.getName());

    private final int k;

    private final int [][][][] colorsArrays;
    private final Map<Thread, Integer> threadToIdx = new ConcurrentHashMap<>();
    private final AtomicInteger idxCounter = new AtomicInteger();

    public BufferedImageToTopKArray(int k, int numberOfThreads) {
        this.k = k;
        this.colorsArrays = new int[numberOfThreads][256][256][256];
    }

    private int[][][] getColorsArray() {
        Integer idx = threadToIdx.computeIfAbsent(Thread.currentThread(), i -> idxCounter.getAndIncrement());
        return colorsArrays[idx];
    }


    @Override
    public Operator<Pair<String, BufferedImage>, Pair<String, List<Pair<Color, Integer>>>, Exception> get() {
        return Operator.of("Calculating top K", this::processImage);
    }

    private Pair<String, List<Pair<Color, Integer>>> processImage(Pair<String, BufferedImage> p) {
        logger.log(Level.FINE, "Processing image " + p.getFirst() + " on thread " + Thread.currentThread().getName());
        List<Pair<Color, Integer>> result = getTopKColor(p.getSecond());
        return Pair.of(p.getFirst(), result);
    }

    List<Pair<Color, Integer>> getTopKColor(BufferedImage t) {
        int[][][] colors = processImage(t);
        List<ComparablePairByValue<Color, Integer>> topK = getTopK(colors);
        return topK.stream().map(p -> Pair.of(p.getK(), p.getV())).collect(Collectors.toList());
    }

    private List<ComparablePairByValue<Color, Integer>> getTopK(int[][][] colors) {
        Stream<ComparablePairByValue<Color, Integer>> stream = getStream(colors);
        return stream.collect(Comparators.greatest(k, ComparablePairByValue::compareTo));
    }

    private Stream<ComparablePairByValue<Color, Integer>> getStream(int[][][] colors) {
        return IntStream.range(0, 256 * 256 * 256)
                .mapToObj(i -> getColorIntegerComparablePairByValue(colors, i))
                .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()));
    }

    private Optional<ComparablePairByValue<Color, Integer>> getColorIntegerComparablePairByValue(int[][][] colors, int i) {
        int r = (i / (256 * 256)) % 256;
        int g = (i / 256) % 256;
        int b = i % 256;
        int count = colors[r][g][b];
        colors[r][g][b] = 0;
        return count > 0
                ? Optional.of(ComparablePairByValue.of(new Color(r, g, b), count))
                : Optional.empty();
    }

    private int[][][] processImage(BufferedImage image) {
        int[][][] colors = getColorsArray();
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                colors[r][g][b]++;
            }
        }
        return colors;
    }


}
