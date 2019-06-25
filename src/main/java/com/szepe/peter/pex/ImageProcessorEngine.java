package com.szepe.peter.pex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageProcessorEngine {

    private final long[][][] colors = new long[256][256][256];
    private final String imageUrl;

    public ImageProcessorEngine(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private BufferedImage readImage() throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    public void process() throws IOException {
        processImage();

        Map<Color, Long> color2Count = new HashMap<>();
        for (int r = 0; r < 255; ++r) {
            for (int g = 0; g < 255; ++g) {
                for (int b = 0; b < 255; ++b) {
                    long count = colors[r][g][b];
                    if (count > 0) {
                        color2Count.put(new Color(r, g, b), count);
                    }
                }
            }
        }
        System.out.println(color2Count);
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
}
