package com.szepe.peter.pex.rx;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BufferedImageToTopKArray implements OperatorProvider<Pair<String, BufferedImage>, Pair<String, List<Pair<Color, Integer>>>, Exception> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopKArray.class.getName());

    private final int k;

    public BufferedImageToTopKArray(int k) {
        this.k = k;
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
        List<ComparablePairByValue<Color, Integer>> list = new LinkedList();
        for (int r = 0; r < 256; ++r) {
            for (int g = 0; g < 256; ++g) {
                for (int b = 0; b < 256; ++b) {
                    int count = colors[r][g][b];
                    if (count > 0) {
                        list.add(new ComparablePairByValue<>(new Color(r, g, b), count));
                    }
                }
            }
        }
        return list.stream();
    }

    private int[][][] processImage(BufferedImage image) {
        int[][][] colors = new int[256][256][256];
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
