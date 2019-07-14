package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BufferedImageToTopKOperatorProvider implements OperatorProvider<Pair<String, BufferedImage>, Pair<String, List<Pair<Integer, Integer>>>, Exception> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopKOperatorProvider.class.getName());

    private final BufferedImageToTopK logic;

    public BufferedImageToTopKOperatorProvider(BufferedImageToTopK logic) {
        this.logic = logic;
    }

    @Override
    public Operator<Pair<String, BufferedImage>, Pair<String, List<Pair<Integer, Integer>>>, Exception> get() {
        return Operator.of("Calculating top K", this::processImage);
    }

    private Pair<String, List<Pair<Integer, Integer>>> processImage(Pair<String, BufferedImage> p) {
        logger.log(Level.FINE, "Processing image " + p.getFirst() + " on thread " + Thread.currentThread().getName());
        List<Pair<Integer, Integer>> result = logic.getTopKColor(p.getSecond());
        return Pair.of(p.getFirst(), result);
    }

}
