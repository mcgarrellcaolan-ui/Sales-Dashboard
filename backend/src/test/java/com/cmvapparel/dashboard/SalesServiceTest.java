package com.cmvapparel.dashboard;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SalesServiceTest {

    private SalesService makeService() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("CP jacket", LocalDate.of(2026, 7, 20), 100, 40));
        items.add(new Item("SI overshirt", LocalDate.of(2026, 6, 5), 150, 80));
        items.add(new Item("Napa fleece", null, 0, 0)); // not sold yet
        items.add(new Item("SI jumper", LocalDate.of(2026, 7, 2), 60, 20));
        items.add(new Item("CP goggle jacket", LocalDate.of(2026, 8, 15), 200, 90));
        return new SalesService(items);
    }

    @Test
    void summary() {
        SalesService service = makeService();
        assertEquals(510, service.getRevenue(), 0.001);
        assertEquals(230, service.getProfit(), 0.001);
        assertEquals(4, service.getItemsSold());
        assertEquals(1, service.getInStock());
    }

    @Test
    void brandsRankedByRevenue() {
        ArrayList<BrandTotal> brands = makeService().getBrands();
        assertEquals("CP Company", brands.get(0).brand);
        assertEquals("Stone Island", brands.get(1).brand);
        assertEquals(2, brands.get(0).items);
    }

    @Test
    void topByProfit() {
        ArrayList<Item> top = makeService().getTopByProfit(2);
        assertEquals("CP goggle jacket", top.get(0).getDescription());
        assertEquals("SI overshirt", top.get(1).getDescription());
    }

    @Test
    void soldBetweenIncludesBothDates() {
        SalesService service = makeService();
        assertEquals(2, service.getSoldBetween(LocalDate.of(2026, 7, 2), LocalDate.of(2026, 7, 20)).size());
        assertEquals(0, service.getSoldBetween(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)).size());
    }

    @Test
    void brands() {
        assertEquals("Stone Island", Brand.of("Stone Island ghost overshirt"));
        assertEquals("Stone Island", Brand.of("navy SI jumper"));
        assertEquals("CP Company", Brand.of("C.P. Company goggle jacket"));
        assertEquals("CP Company", Brand.of("cp shell"));
        assertEquals("Napapijri", Brand.of("Napa Rainforest"));
        assertEquals("Other", Brand.of("Arc'teryx fleece"));
        assertEquals("Other", Brand.of("classic tee")); // "si" inside a word doesn't count
    }

    @Test
    void readsTheSampleFile() throws Exception {
        ArrayList<Item> items = ExcelReader.read("../data/sample-data.xlsx");
        assertEquals(30, items.size());
        assertEquals("Black Stone Island crewneck jumper", items.get(0).getDescription());
        assertEquals(LocalDate.of(2026, 6, 7), items.get(0).getSoldOn());

        // same numbers as the website shows
        SalesService service = new SalesService(items);
        assertEquals(1659, service.getRevenue(), 0.001);
        assertEquals(20, service.getItemsSold());
        assertEquals(10, service.getInStock());
    }
}
