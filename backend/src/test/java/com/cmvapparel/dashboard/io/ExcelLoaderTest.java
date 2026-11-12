package com.cmvapparel.dashboard.io;

import com.cmvapparel.dashboard.model.Item;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelLoaderTest {

    @Test
    void readsTheSampleFile() throws Exception {
        List<Item> items = ExcelLoader.load("../data/sample-data.xlsx");
        assertEquals(30, items.size());

        Item first = items.get(0);
        assertEquals("Black Stone Island crewneck jumper", first.description());
        assertEquals(LocalDate.of(2026, 6, 7), first.soldOn());
        assertEquals(65, first.price(), 0.001);
    }

    @Test
    void excelDates() {
        assertEquals(LocalDate.of(2026, 6, 7), ExcelLoader.toDate("46180"));
        assertNull(ExcelLoader.toDate(""));
        assertNull(ExcelLoader.toDate(null));
    }
}
