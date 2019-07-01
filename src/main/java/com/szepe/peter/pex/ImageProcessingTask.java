package com.szepe.peter.pex;

import java.awt.image.BufferedImage;

public class ImageProcessingTask {

    private final String imageUrl;
    private final BufferedImage image;

    public ImageProcessingTask(String imageUrl, BufferedImage image) {
        this.imageUrl = imageUrl;
        this.image = image;
    }
}
