package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import rx.Observable;
import rx.Subscriber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteArrayToBufferedImage implements Observable.Operator<Pair<String, BufferedImage>, Pair<String, byte[]>> {

    private final static Logger logger = Logger.getLogger(ByteArrayToBufferedImage.class.getName());

    @Override
    public Subscriber<? super Pair<String, byte[]>> call(Subscriber<? super Pair<String, BufferedImage>> subscriber) {
        return new Subscriber<Pair<String, byte[]>>() {
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
            public void onNext(Pair<String, byte[]> t) {
                if (!subscriber.isUnsubscribed()) {
                    URL url = null;
                    try {
                        logger.log(Level.FINE, "processing image " + t.getFirst() + " on thread " + Thread.currentThread().getName());
                        ByteArrayInputStream bais = new ByteArrayInputStream(t.getSecond());
                        BufferedImage image = ImageIO.read(bais);
                        subscriber.onNext(Pair.of(t.getFirst(), image));
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Unable to read image as BufferedImage " + t.getFirst());
                    }
                }
            }
        };
    }
}
