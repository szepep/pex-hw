package com.szepe.peter.pex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.szepe.peter.pex"})
public class Bootstrap implements CommandLineRunner {

    @Autowired
    ImageProcessor imageProcessor;

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        imageProcessor.process();
    }
}
