package com.szepe.peter.pex.impl;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BImageToTopKArray implements BufferedImageToTopK {

    private final static Logger logger = Logger.getLogger(BImageToTopKArray.class.getName());

    private final int k;

    private final int[][][][] colorsArrays;
    private final Map<Thread, Integer> threadToIdx = new ConcurrentHashMap<>();
    private final AtomicInteger idxCounter = new AtomicInteger();

    public BImageToTopKArray(int k, int numberOfThreads) {
        this.k = k;
        this.colorsArrays = new int[numberOfThreads][256][256][256];
    }

    private int[][][] getColorsArray() {
        Integer idx = threadToIdx.computeIfAbsent(Thread.currentThread(), i -> idxCounter.getAndIncrement());
        return colorsArrays[idx];
    }

    @Override
    public List<Pair<Integer, Integer>> getTopKColor(BufferedImage t) {
        int[][][] colors = processImage(t);
        List<ComparablePairByValue<Integer, Integer>> topK = getTopK(colors);
        return topK.stream().map(p -> Pair.of(p.getK(), p.getV())).collect(Collectors.toList());
    }

    private List<ComparablePairByValue<Integer, Integer>> getTopK(int[][][] colors) {
        return getStream(colors).collect(Comparators.greatest(k, ComparablePairByValue::compareTo));
    }

    private Stream<ComparablePairByValue<Integer, Integer>> getStream(int[][][] colors) {
        return IntStream.range(0, 256 * 256 * 256)
                .mapToObj(i -> getColorIntegerComparablePairByValue(colors, i))
                .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()));
    }

    private Optional<ComparablePairByValue<Integer, Integer>> getColorIntegerComparablePairByValue(int[][][] colors, int i) {
        int r = (i / (256 * 256)) % 256;
        int g = (i / 256) % 256;
        int b = i % 256;
        int count = colors[r][g][b];
        colors[r][g][b] = 0;
        return count > 0
                ? Optional.of(ComparablePairByValue.of(color(r, g, b), count))
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

    private int color(int r, int g, int b) {
        return ((0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }

}
