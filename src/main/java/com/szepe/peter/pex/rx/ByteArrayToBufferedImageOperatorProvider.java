package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ByteArrayToBufferedImageOperatorProvider implements OperatorProvider<Pair<String, byte[]>, Pair<String, BufferedImage>, IOException> {

    private final static Logger logger = Logger.getLogger(ByteArrayToBufferedImageOperatorProvider.class.getName());

    ByteArrayToBufferedImageOperatorProvider() {
    }

    @Override
    public Operator<Pair<String, byte[]>, Pair<String, BufferedImage>, IOException> get() {
        return Operator.of("Calculating BufferedImage from byte array",
                this::createBufferedImage,
                e -> logger.log(Level.WARNING, "Unable to read image as BufferedImage " + e.getValue(), e.getException()));
    }

    private Pair<String, BufferedImage> createBufferedImage(Pair<String, byte[]> p) throws IOException {
        logger.log(Level.FINE, "Creating BufferedImage from " + p.getFirst() + " on thread " + Thread.currentThread().getName());
        ByteArrayInputStream bais = new ByteArrayInputStream(p.getSecond());
        BufferedImage image = ImageIO.read(bais);
        return Pair.of(p.getFirst(), image);
    }
}
