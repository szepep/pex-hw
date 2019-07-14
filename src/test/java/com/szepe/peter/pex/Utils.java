package com.szepe.peter.pex;

import java.awt.*;
import java.util.Random;

public class Utils {

    public static Color randomColor(Random random) {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return new Color(r, g, b);
    }
}
