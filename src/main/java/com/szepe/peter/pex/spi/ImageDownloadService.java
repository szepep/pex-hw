package com.szepe.peter.pex.spi;

import java.io.IOException;

public interface ImageDownloadService {

    byte[] download(String path) throws IOException;
}
