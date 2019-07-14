package com.szepe.peter.pex;

import com.szepe.peter.pex.api.Pair;
import com.szepe.peter.pex.spi.ImageDownloadService;
import com.szepe.peter.pex.spi.InputReader;
import com.szepe.peter.pex.spi.OutputWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
class ImageProcessorTest {

    @Autowired
    ImageProcessor underTest;

    @MockBean
    ImageDownloadService downloadService;

    @MockBean
    OutputWriter outputWriter;

    private Map<String, Pair<BufferedImage, TreeMap<Integer, Set<Color>>>> urlToImage = new ConcurrentHashMap<>();

    @Test
    public void testLogic() throws IOException, InputReader.InputReaderException, OutputWriter.OutputWriterException {
        int maxColors = 100;

        List<String> errors = new CopyOnWriteArrayList<>();

        Mockito.doAnswer(invocation -> {
            String url = invocation.getArgument(0);
            Random r = new Random(url.hashCode());
            Pair<BufferedImage, TreeMap<Integer, Set<Color>>> bufferedImageMapPair = Utils.generateImage(r, maxColors);
            urlToImage.put(url, bufferedImageMapPair);
            return getBytes(bufferedImageMapPair.getFirst());
        }).when(downloadService).download(anyString());

        Mockito.doAnswer(invocationOnMock -> {
            String line = invocationOnMock.getArgument(0);
            verifyColors(errors, line);
            return null;
        }).when(outputWriter).write(anyString());

        underTest.process();
        assertTrue(errors.isEmpty(), errors.toString());
    }

    private void verifyColors(List<String> errors, String line) {
        String[] split = line.split(";");
        String url = split[0];
        TreeMap<Integer, Set<Color>> colors = urlToImage.get(url).getSecond();
        for (int i = 1; i < split.length; ++i) {
            String hex = split[i];
            Color color = Color.decode(hex);

            Map.Entry<Integer, Set<Color>> firstEntry = colors.firstEntry();
            Integer count = firstEntry.getKey();
            boolean colorPresent = firstEntry.getValue().remove(color);
            if (!colorPresent) {
                errors.add("Processing url " + url + " failed");
            }

            if (firstEntry.getValue().isEmpty()) {
                colors.remove(count);
            }
        }
    }

    private byte[] getBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

}