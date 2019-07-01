package com.szepe.peter.pex;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.ComparablePairByValue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImageProcessingTask implements Runnable{

    private final static Logger logger = Logger.getLogger(ImageProcessingTask.class.getName());

    public static ImageProcessingTask of(String imageUrl, BufferedImage image, int k) {
        return new ImageProcessingTask(imageUrl, image, k);
    }

    private final String imageUrl;
    private final BufferedImage image;
    private final int k;

    private ImageProcessingTask(String imageUrl, BufferedImage image, int k) {
        this.imageUrl = imageUrl;
        this.image = image;
        this.k = k;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void run() {
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
        List<ComparablePairByValue<Color, Integer>> list = new ArrayList<>();
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

        List<ComparablePairByValue<Color, Integer>> collect = list.stream()
                .collect(Comparators.greatest(k, ComparablePairByValue::compareTo));

        logger.info(collect.toString());
    }
}
