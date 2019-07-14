package com.szepe.peter.pex.impl;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.BufferedImageToTopK;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BImageToTopKMap implements BufferedImageToTopK {

    private final static Logger logger = Logger.getLogger(BImageToTopKMap.class.getName());

    private final int k;

    public BImageToTopKMap(int k) {
        this.k = k;
    }

    @Override
    public List<Pair<Integer, Integer>> getTopKColor(BufferedImage t) {
        Map<Integer, Integer> colors = new HashMap<>();
        BufferedImage image = t;
        for (int w = 0; w < image.getWidth(); ++w) {
            for (int h = 0; h < image.getHeight(); ++h) {
                int color = image.getRGB(w, h);
                Integer cnt = colors.getOrDefault(color, 0);
                ++cnt;
                colors.put(color, cnt);
            }
        }
        return colors.entrySet().stream().map(e -> ComparablePairByValue.of(e.getKey(), e.getValue()))
                .collect(Comparators.greatest(k, ComparablePairByValue::compareTo))
                .stream().map(p -> Pair.of(p.getK(), p.getV())).collect(Collectors.toList());
    }

}
