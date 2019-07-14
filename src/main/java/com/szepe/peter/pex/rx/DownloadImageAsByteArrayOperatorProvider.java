package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.ImageDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class DownloadImageAsByteArrayOperatorProvider implements OperatorProvider<String, Pair<String, byte[]>, IOException> {

    private final static Logger logger = Logger.getLogger(DownloadImageAsByteArrayOperatorProvider.class.getName());

    private final ImageDownloadService downloadService;

    @Autowired
    DownloadImageAsByteArrayOperatorProvider(ImageDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Override
    public Operator<String, Pair<String, byte[]>, IOException> get() {
        return Operator.of("Downloading images",
                this::downloadImage,
                e -> logger.log(Level.WARNING, "Unable to download image" + e.getValue(), e.getException()));
    }

    private Pair<String, byte[]> downloadImage(String url) throws IOException {
        logger.log(Level.FINE, "Downloading image " + url + " on thread " + Thread.currentThread().getName());
        return Pair.of(url, downloadService.download(url));
    }
}