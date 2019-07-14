package com.szepe.peter.pex;

import com.szepe.peter.pex.impl.BImageToTopKMap;
import com.szepe.peter.pex.impl.OutputWriterToConsole;
import com.szepe.peter.pex.spi.BufferedImageToTopK;
import com.szepe.peter.pex.spi.OutputWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackageClasses = Config.class)
@PropertySource(value = "classpath:application.properties")
public class Config {

    @Value("${top_k}")
    int topK;

    @Bean
    public BufferedImageToTopK topKProcessor() {
        return new BImageToTopKMap(topK);
    }

    @Bean
    public OutputWriter outputWriter() {
        return new OutputWriterToConsole();
    }
}
