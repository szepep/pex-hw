package com.szepe.peter.pex;

import com.google.common.collect.Ordering;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class ImageProcessorEngine {

    private final long[][][] colors = new long[256][256][256];
    private final String imageUrl;
    private final int k;

    public ImageProcessorEngine(String imageUrl, int k) {
        this.imageUrl = imageUrl;
        this.k = k;
    }

    private BufferedImage readImage() throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    public List<Pair<Color, Long>> process() throws IOException {
        processImage();

        List<Pair<Color, Long>> list = new ArrayList<>();
        for (int r = 0; r < 256; ++r) {
            for (int g = 0; g < 256; ++g) {
                for (int b = 0; b < 256; ++b) {
                    long count = colors[r][g][b];
                    if (count > 0) {
                        list.add(new Pair<>(new Color(r, g, b), count));
                    }
                }
            }
        }
        List<Pair<Color, Long>> pairs = Ordering.natural().greatestOf(list, k);

        return pairs;
    }

    private void processImage() throws IOException {
        BufferedImage image = readImage();
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                colors[r][g][b]++;
            }
        }
    }


    public static class Pair<K, V extends Comparable<V>> implements Comparable<Pair<K, V>> {

        private final K k;
        private final V v;

        public Pair(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public int compareTo(Pair<K, V> o) {
            return this.v.compareTo(o.v);
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "k=" + k +
                    ", v=" + v +
                    '}';
        }
    }
}
