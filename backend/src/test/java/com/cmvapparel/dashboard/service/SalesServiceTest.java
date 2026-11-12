package com.cmvapparel.dashboard.service;

import com.cmvapparel.dashboard.io.CsvLoader;
import com.cmvapparel.dashboard.model.Item;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SalesServiceTest {

    private static Item sold(String name, String date, double price, double profit) {
        return new Item(name, "M", null, price - profit, LocalDate.parse(date), price, profit);
    }

    private final SalesService service = new SalesService(List.of(
            sold("CP jacket", "2026-07-20", 100, 40),
            sold("SI overshirt", "2026-06-05", 150, 80),
            new Item("Napa fleece", "L", null, 30, null, 0, 0), // not sold
            sold("SI jumper", "2026-07-02", 60, 20),
            sold("CP goggle jacket", "2026-08-15", 200, 90)));

    @Test
    void summary() {
        SalesService.Summary s = service.summary();
        assertEquals(510, s.revenue(), 0.001);
        assertEquals(230, s.profit(), 0.001);
        assertEquals(4, s.itemsSold());
        assertEquals(1, s.inStock());
    }

    @Test
    void brandsRankedByRevenue() {
        List<SalesService.BrandTotal> brands = service.brands();
        assertEquals("CP Company", brands.get(0).brand);
        assertEquals("Stone Island", brands.get(1).brand);
        assertEquals(2, brands.get(0).items);
    }

    @Test
    void topByProfit() {
        List<Item> top = service.topByProfit(2);
        assertEquals("CP goggle jacket", top.get(0).description());
        assertEquals("SI overshirt", top.get(1).description());
    }

    @Test
    void soldBetweenIncludesBothDates() {
        List<Item> july = service.soldBetween(LocalDate.of(2026, 7, 2), LocalDate.of(2026, 7, 20));
        assertEquals(2, july.size());
        assertTrue(service.soldBetween(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)).isEmpty());
    }

    @Test
    void sampleCsvMatchesWebsite() throws Exception {
        SalesService real = new SalesService(CsvLoader.load("../data/sales.csv"));
        assertEquals(1659, real.summary().revenue(), 0.001);
        assertEquals(20, real.summary().itemsSold());
        assertEquals(10, real.summary().inStock());
    }
}
