package com.szepe.peter.pex.rx;

import com.google.common.io.ByteStreams;
import com.szepe.peter.pex.api.Pair;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadImageAsByteArray implements Observable.Operator<Pair<String, byte[]>, String> {

    private final static Logger logger = Logger.getLogger(DownloadImageAsByteArray.class.getName());

    @Override
    public Subscriber<? super String> call(Subscriber<? super Pair<String, byte[]>> subscriber) {
        return new Subscriber<String>() {
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
            public void onNext(String s) {
                if (!subscriber.isUnsubscribed()) {
                    URL url = null;
                    try {
                        logger.log(Level.FINE, "processing image " + s + " on thread " + Thread.currentThread().getName());
                        url = new URL(s);
                        byte[] targetArray = ByteStreams.toByteArray(url.openStream());
                        subscriber.onNext(Pair.of(s, targetArray));
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Unable to load image " + s);
                    }
                }
            }
        };
    }
}
