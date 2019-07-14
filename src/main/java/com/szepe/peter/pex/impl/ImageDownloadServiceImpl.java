package com.szepe.peter.pex.impl;

import com.google.common.io.ByteStreams;
import com.szepe.peter.pex.spi.ImageDownloadService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
class ImageDownloadServiceImpl implements ImageDownloadService {
    @Override
    public byte[] download(String path) throws IOException {
        URL url = new URL(path);
        return ByteStreams.toByteArray(url.openStream());
    }
}
