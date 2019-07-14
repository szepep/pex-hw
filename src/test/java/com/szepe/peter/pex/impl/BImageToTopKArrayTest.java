package com.szepe.peter.pex.impl;

class BImageToTopKArrayTest extends AbstractBufferedImageToTopKTest<BImageToTopKArray> {


    BImageToTopKArrayTest() {
        super(k -> new BImageToTopKArray(k, 1));
    }
}