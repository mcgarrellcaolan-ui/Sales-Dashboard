package com.cmvapparel.dashboard.io;

import com.cmvapparel.dashboard.model.Item;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CsvLoaderTest {

    @Test
    void soldItem() {
        Item item = CsvLoader.parseLine("Stone Island reflective jacket,L,2026-07-16,70,2026-07-26,150,80");
        assertEquals("Stone Island reflective jacket", item.description());
        assertEquals(LocalDate.of(2026, 7, 26), item.soldOn());
        assertEquals(150, item.price(), 0.001);
        assertTrue(item.isSold());
    }

    @Test
    void unsoldItem() {
        Item item = CsvLoader.parseLine("CP Company cargo pants,32,2026-08-24,27,,,");
        assertNull(item.soldOn());
        assertFalse(item.isSold());
        assertEquals(27, item.cost(), 0.001);
    }
}
