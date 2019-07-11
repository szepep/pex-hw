package com.szepe.peter.pex.rx;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BufferedImageToTopK implements OperatorProvider<Pair<String, BufferedImage>, Pair<String, List<Pair<Color, Integer>>>, Exception> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopK.class.getName());

    private final int k;

    public BufferedImageToTopK(int k) {
        this.k = k;
    }

    @Override
    public Operator<Pair<String, BufferedImage>, Pair<String, List<Pair<Color, Integer>>>, Exception> get() {
        return Operator.of("Calculating top K", this::processImage);
    }

    Pair<String, List<Pair<Color, Integer>>> processImage(Pair<String, BufferedImage> p) {
        logger.log(Level.FINE, "Processing image " + p.getFirst() + " on thread " + Thread.currentThread().getName());
        List<Pair<Color, Integer>> result = getTopKColor(p.getSecond());
        return Pair.of(p.getFirst(), result);
    }

    private List<Pair<Color, Integer>> getTopKColor(BufferedImage t) {
        Map<Integer, Integer> colors = new HashMap<>();
        BufferedImage image = t;
        for (int w = 0; w < image.getWidth(); ++w) {
            for (int h = 0; h < image.getHeight(); ++h) {
                int color = image.getRGB(w, h);
                Integer cnt = colors.getOrDefault(color, 0);
                ++cnt;
                colors.put(color, cnt);
            }
        }
        return colors.entrySet().stream().map(e -> ComparablePairByValue.of(e.getKey(), e.getValue()))
                .collect(Comparators.greatest(k, ComparablePairByValue::compareTo))
                .stream().map(p -> Pair.of(new Color(p.getK()), p.getV())).collect(Collectors.toList());
    }

}
