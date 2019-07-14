package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BufferedImageToTopKOperatorProvider implements OperatorProvider<Pair<String, BufferedImage>, Pair<String, List<Color>>, Exception> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopKOperatorProvider.class.getName());

    private final BufferedImageToTopK logic;

    @Autowired
    BufferedImageToTopKOperatorProvider(BufferedImageToTopK logic) {
        this.logic = logic;
    }

    @Override
    public Operator<Pair<String, BufferedImage>, Pair<String, List<Color>>, Exception> get() {
        return Operator.of("Calculating top K", this::processImage);
    }

    private Pair<String, List<Color>> processImage(Pair<String, BufferedImage> p) {
        logger.log(Level.FINE, "Processing image " + p.getFirst() + " on thread " + Thread.currentThread().getName());
        List<Color> result = logic.getTopKColor(p.getSecond()).stream()
                .map(e -> e.getFirst())
                .collect(Collectors.toList());
        return Pair.of(p.getFirst(), result);
    }

}
