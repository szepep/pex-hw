package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    static Pair<BufferedImage, Map<Integer, Set<Color>>> generateImage(Random random, int numberOfColors) {
        int max = 100;
        int width = 1 + random.nextInt(max);
        int height = 1 + random.nextInt(max);

        List<Color> colors = new ArrayList<>(numberOfColors);
        for (int i = 0; i< numberOfColors; ++i) {
            colors.add(randomColor(random));
        }

        Map<Color, Integer> colorMap = new HashMap<>();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color c = colors.get(random.nextInt(colors.size()));
                colorMap.put(c, colorMap.getOrDefault(c, 0) + 1);
                img.setRGB(x, y, c.getRGB());
            }
        }

        List<ComparablePairByValue<Color, Integer>> list = colorMap.entrySet().stream()
                .map(e -> ComparablePairByValue.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        Collections.sort(list, Collections.reverseOrder());
        Map<Integer, Set<Color>> map = new HashMap<>();
        for (ComparablePairByValue<Color, Integer> p : list) {
            Integer count = p.getV();
            Set<Color> cs = map.get(count);
            if (cs == null) {
                cs = new HashSet<>();
                map.put(count, cs);
            }
            cs.add(p.getK());
        }
        return Pair.of(img, map);
    }

    private static Color randomColor(Random random) {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return new Color(r, g, b);
    }
}
