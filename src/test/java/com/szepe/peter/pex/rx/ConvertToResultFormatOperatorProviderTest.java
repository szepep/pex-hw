package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.Utils;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertToResultFormatOperatorProviderTest {

    @Test
    void convertColorToHexadecimal() {
        long seed = System.currentTimeMillis();
        Random r = new Random(seed);

        for (int i = 0; i < 100; ++i) {
            Color c = Utils.randomColor(r);
            ConvertToResultFormatOperatorProvider underTest = new ConvertToResultFormatOperatorProvider();
            String hex = underTest.convertColorToHexadecimal(c);
            assertEquals(c, Color.decode(hex), "Failed with seed " + seed);
        }
    }
}