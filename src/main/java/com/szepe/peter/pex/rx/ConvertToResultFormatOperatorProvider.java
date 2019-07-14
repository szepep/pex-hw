package com.szepe.peter.pex.rx;

import com.szepe.peter.pex.api.Pair;

import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvertToResultFormatOperatorProvider implements OperatorProvider<Pair<String, List<Color>>, String, Exception> {

    private final static Logger logger = Logger.getLogger(ConvertToResultFormatOperatorProvider.class.getName());

    @Override
    public Operator<Pair<String, List<Color>>, String, Exception> get() {
        return Operator.of("Converting to output format", this::processResult);
    }

    private String processResult(Pair<String, List<Color>> p) {
        String url = p.getFirst();
        logger.log(Level.FINE, "Processing image " + url + " on thread " + Thread.currentThread().getName());
        List<Color> colors = p.getSecond();
        StringBuilder builder = new StringBuilder(url);

        for (Color c : colors) {
            builder.append(";");
            builder.append(convertColorToHexadecimal(c));
        }
        return builder.toString();
    }

    String convertColorToHexadecimal(Color color) {
        String hexColour = Integer.toHexString(color.getRGB() & 0xffffff);
        if (hexColour.length() < 6) {
            hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
        }
        return "#" + hexColour;
    }

}
