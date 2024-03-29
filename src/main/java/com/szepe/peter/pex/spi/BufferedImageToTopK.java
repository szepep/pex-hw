package com.szepe.peter.pex.spi;

import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public interface BufferedImageToTopK {
    List<Pair<Color, Integer>> getTopKColor(BufferedImage t);
}
