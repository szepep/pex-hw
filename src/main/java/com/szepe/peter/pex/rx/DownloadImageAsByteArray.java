package com.szepe.peter.pex.rx;

import com.google.common.io.ByteStreams;
import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.api.Try;
import com.szepe.peter.pex.utils.Watch;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadImageAsByteArray implements OperatorProvider<String, Pair<String, byte[]>, IOException> {

    private final static Logger logger = Logger.getLogger(DownloadImageAsByteArray.class.getName());

    @Override
    public Operator<String, Pair<String, byte[]>, IOException> get() {
        return Operator.of("Downloading images",
                this::downloadImage,
                e -> logger.log(Level.WARNING, "Unable to download image" + e.getValue(), e.getException()));
    }

    Pair<String, byte[]> downloadImage(String u) throws IOException {
        logger.log(Level.FINE, "Downloading image " + u + " on thread " + Thread.currentThread().getName());
        URL url = new URL(u);
        return Pair.of(u,ByteStreams.toByteArray(url.openStream()));
    }
}
