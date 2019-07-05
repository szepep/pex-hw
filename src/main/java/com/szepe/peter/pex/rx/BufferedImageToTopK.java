package com.szepe.peter.pex.rx;

import com.google.common.collect.Comparators;
import com.szepe.peter.pex.api.ComparablePairByValue;
import com.szepe.peter.pex.api.Pair;
import rx.Observable;
import rx.Subscriber;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BufferedImageToTopK implements Observable.Operator<
        Pair<String, List<Pair<Color, Integer>>>, Pair<String, BufferedImage>> {

    private final static Logger logger = Logger.getLogger(BufferedImageToTopK.class.getName());

    private final int k;

    public BufferedImageToTopK(int k) {
        this.k = k;
    }

    @Override
    public Subscriber<? super Pair<String, BufferedImage>> call(
            Subscriber<? super Pair<String, List<Pair<Color, Integer>>>> subscriber) {

        return new Subscriber<Pair<String, BufferedImage>>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(throwable);
                }
            }

            @Override
            public void onNext(Pair<String, BufferedImage> t) {
                if (!subscriber.isUnsubscribed()) {
                    logger.log(Level.FINE, "processing image " + t.getFirst() + " on thread " + Thread.currentThread().getName());
                    List<Pair<Color, Integer>> result = getTopKColor(t.getSecond());
                    subscriber.onNext(Pair.of(t.getFirst(), result));
                }
            }
        };
    }

    private List<Pair<Color, Integer>> getTopKColor(BufferedImage t) {
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
                .stream().map(p -> Pair.of(new Color(p.getK()), p.getV())).collect(Collectors.toList());
    }
}
