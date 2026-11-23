package com.cmvapparel.dashboard;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SalesServiceTest {

    private static final LocalDate BOUGHT = LocalDate.of(2026, 5, 1);

    private SalesService makeService() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("CP jacket", BOUGHT, 60, LocalDate.of(2026, 7, 20), 100, 40));
        items.add(new Item("SI overshirt", BOUGHT, 70, LocalDate.of(2026, 6, 5), 150, 80));
        items.add(new Item("Napa fleece", BOUGHT, 30, null, 0, 0)); // not sold yet
        items.add(new Item("SI jumper", BOUGHT, 40, LocalDate.of(2026, 7, 2), 60, 20));
        items.add(new Item("CP goggle jacket", BOUGHT, 110, LocalDate.of(2026, 8, 15), 200, 90));
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
    void slowStockShowsOldUnsoldItemsOnly() {
        LocalDate today = LocalDate.of(2026, 11, 1);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("bought last week", today.minusDays(7), 20, null, 0, 0));
        items.add(new Item("bought 90 days ago", today.minusDays(90), 30, null, 0, 0));
        items.add(new Item("bought 120 days ago", today.minusDays(120), 40, null, 0, 0));
        items.add(new Item("old but sold", today.minusDays(200), 50, today.minusDays(10), 90, 40));

        ArrayList<Item> slow = new SalesService(items).getSlowStock(60, today);

        assertEquals(2, slow.size());
        assertEquals("bought 120 days ago", slow.get(0).getDescription()); // oldest first
        assertEquals("bought 90 days ago", slow.get(1).getDescription());
        assertEquals(90, slow.get(1).getDaysInStock(today));
    }

    @Test
    void slowStockIncludesItemExactlyOnTheLimit() {
        LocalDate today = LocalDate.of(2026, 11, 1);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("exactly 60 days", today.minusDays(60), 20, null, 0, 0));
        items.add(new Item("59 days", today.minusDays(59), 20, null, 0, 0));

        ArrayList<Item> slow = new SalesService(items).getSlowStock(60, today);
        assertEquals(1, slow.size());
        assertEquals("exactly 60 days", slow.get(0).getDescription());
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

        // the 10 unsold items were bought between 31 July and 27 August
        assertEquals(10, service.getSlowStock(60, LocalDate.of(2026, 11, 1)).size());
        assertEquals(0, service.getSlowStock(60, LocalDate.of(2026, 9, 1)).size());
    }
}
