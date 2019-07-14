package com.szepe.peter.pex.impl;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractBufferedImageToTopKTest<B extends BufferedImageToTopK> {

    private final Function<Integer, B> underTestSupplier;

    AbstractBufferedImageToTopKTest(Function<Integer, B> underTestSupplier) {
        this.underTestSupplier = underTestSupplier;
    }

    @Test
    public void testSingleImage() {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        int numberOfColors = 100;
        int k = 3;
        B underTest = underTestSupplier.apply(k);
        testImage(r, seed, numberOfColors, underTest);
    }

    @Test
    public void testTwoImagesSingleImage() {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        int numberOfColors = 100;
        int k = 3;
        B underTest = underTestSupplier.apply(k);
        testImage(r, seed, numberOfColors, underTest);
        testImage(r, seed, numberOfColors, underTest);
    }

    @Test
    public void testAllColors() {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        int numberOfColors = 100;
        B underTest = underTestSupplier.apply(numberOfColors);
        testImage(r, seed, numberOfColors, underTest);
        testImage(r, seed, numberOfColors, underTest);
    }

    private void testImage(Random r, long seed, int numberOfColors, BufferedImageToTopK logic) {
        Pair<BufferedImage, Map<Integer, Set<Color>>> imgAndColorCounts = Utils.generateImage(r, numberOfColors);
        BufferedImage img = imgAndColorCounts.getFirst();
        Map<Integer, Set<Color>> colorMap = imgAndColorCounts.getSecond();

        List<Pair<Integer, Integer>> topKColor = logic.getTopKColor(img);
        int prevCount = Integer.MAX_VALUE;
        for (Pair<Integer, Integer> p : topKColor) {
            Color color = new Color(p.getFirst());
            Integer count = p.getSecond();
            assertTrue(prevCount >= count, "The result is not ordered, failed with seed " + seed);
            assertTrue(colorMap.get(count).contains(color), "Not expected color, failed with seed " + seed);
            prevCount = count;
        }
    }

}