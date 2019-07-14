package com.szepe.peter.pex.impl;

class BImageToTopKMapTest extends AbstractBufferedImageToTopKTest<BImageToTopKMap> {

    BImageToTopKMapTest() {
        super(k -> new BImageToTopKMap(k));
    }

}