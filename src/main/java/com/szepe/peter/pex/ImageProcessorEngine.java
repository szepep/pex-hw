package com.szepe.peter.pex;

import com.google.common.collect.Comparators;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ImageProcessorEngine {

    private final static Logger logger = Logger.getLogger(ImageProcessorEngine.class.getName());
    private final static StopWatch clock = new StopWatch();

    public static void printStats() {
        logger.info(clock.prettyPrint());
    }

    private final String imageUrl;
    private final int k;

    public ImageProcessorEngine(String imageUrl, int k) {
        this.imageUrl = imageUrl;
        this.k = k;
    }

    private Optional<BufferedImage> readImage(){
        try {
            URL url = new URL(imageUrl);
            BufferedImage read = ImageIO.read(url);
            return Optional.of(read);
        } catch (IOException e) {
            logger.warning("Unable to read image " + imageUrl);
            return Optional.empty();
        }
    }

    public List<Pair<Color, Integer>> process() throws IOException {
        Optional<BufferedImage> image = clock.start("Download image", () -> readImage());
        if (image.isPresent()) {
            int[][][] colors = clock.start("Count colors", () -> processImage(image.get()));
            List<Pair<Color, Integer>> pairs = clock.start("Select top K", () -> getTopK(colors));
            return pairs;
        } else {
            return Collections.emptyList();
        }
    }

    private List<Pair<Color, Integer>> getTopK(int[][][] colors) {
        List<Pair<Color, Integer>> list = new ArrayList<>();
        for (int r = 0; r < 256; ++r) {
            for (int g = 0; g < 256; ++g) {
                for (int b = 0; b < 256; ++b) {
                    int count = colors[r][g][b];
                    if (count > 0) {
                        list.add(new Pair<>(new Color(r, g, b), count));
                    }
                }
            }
        }

        return list.stream().collect(Comparators.greatest(k, Pair::compareTo));
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
