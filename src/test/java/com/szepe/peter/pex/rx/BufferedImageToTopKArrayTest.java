package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BufferedImageToTopKArrayTest {

    @Test
    public void testSingleImage() {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        int numberOfColors = 100;
        int k = 3;
        testImage(r, seed, numberOfColors, k);
    }

    @Test
    public void testTwoImagesSingleImage() {
        long seed = System.nanoTime();
        Random r = new Random(seed);
        int numberOfColors = 100;
        int k = 3;
        testImage(r, seed, numberOfColors, k);
        testImage(r, seed, numberOfColors, k);
    }

    private void testImage(Random r, long seed, int numberOfColors, int k) {
        Pair<BufferedImage, Map<Integer, Set<Color>>> imgAndColorCounts = Utils.generateImage(r, numberOfColors);
        BufferedImage img = imgAndColorCounts.getFirst();
        Map<Integer, Set<Color>> colorMap = imgAndColorCounts.getSecond();

        List<Pair<Color, Integer>> topKColor = new BufferedImageToTopKArray(k, 1).getTopKColor(img);
        int prevCount = Integer.MAX_VALUE;
        for (Pair<Color, Integer> p : topKColor) {
            Color color = p.getFirst();
            Integer count = p.getSecond();
            assertTrue(prevCount >= count, "The result is not ordered, failed with seed " + seed);
            assertTrue(colorMap.get(count).contains(color), "Not expected color, failed with seed " + seed);
        }
    }
}