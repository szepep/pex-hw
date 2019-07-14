package com.szepe.peter.pex.impl;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.szepe.peter.pex.Utils.randomColor;
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
        Pair<BufferedImage, Map<Integer, Set<Color>>> imgAndColorCounts = generateImage(r, numberOfColors);
        BufferedImage img = imgAndColorCounts.getFirst();
        Map<Integer, Set<Color>> colorMap = imgAndColorCounts.getSecond();

        List<Pair<Color, Integer>> topKColor = logic.getTopKColor(img);
        int prevCount = Integer.MAX_VALUE;
        for (Pair<Color, Integer> p : topKColor) {
            Color color = p.getFirst();
            Integer count = p.getSecond();
            assertTrue(prevCount >= count, "The result is not ordered, failed with seed " + seed);
            assertTrue(colorMap.get(count).contains(color), "Not expected color, failed with seed " + seed);
            prevCount = count;
        }
    }

    Pair<BufferedImage, Map<Integer, Set<Color>>> generateImage(Random random, int numberOfColors) {
        int max = 100;
        int width = 1 + random.nextInt(max);
        int height = 1 + random.nextInt(max);

        List<Color> colors = new ArrayList<>(numberOfColors);
        for (int i = 0; i < numberOfColors; ++i) {
            colors.add(randomColor(random));
        }

        Map<Color, Integer> colorMap = new HashMap<>();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color c = colors.get(random.nextInt(colors.size()));
                colorMap.put(c, colorMap.getOrDefault(c, 0) + 1);
                img.setRGB(x, y, c.getRGB());
            }
        }

        List<ComparablePairByValue<Color, Integer>> list = colorMap.entrySet().stream()
                .map(e -> ComparablePairByValue.of(e.getKey(), e.getValue()))
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        Map<Integer, Set<Color>> map = new HashMap<>();
        for (ComparablePairByValue<Color, Integer> p : list) {
            Integer count = p.getV();
            Set<Color> cs = map.computeIfAbsent(count, k -> new HashSet<>());
            cs.add(p.getK());
        }
        return Pair.of(img, map);
    }

}