package com.cmvapparel.dashboard.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrandClassifierTest {

    @Test
    void brands() {
        assertEquals("Stone Island", BrandClassifier.brandOf("Stone Island ghost overshirt"));
        assertEquals("Stone Island", BrandClassifier.brandOf("navy SI jumper"));
        assertEquals("CP Company", BrandClassifier.brandOf("C.P. Company goggle jacket"));
        assertEquals("CP Company", BrandClassifier.brandOf("cp shell"));
        assertEquals("Napapijri", BrandClassifier.brandOf("Napa Rainforest"));
        assertEquals("Other", BrandClassifier.brandOf("Arc'teryx fleece"));
    }

    @Test
    void siInsideAWordDoesntCount() {
        assertEquals("Other", BrandClassifier.brandOf("classic tee"));
    }
}
