import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SlowStockReportTest {

    // A fixed date, so the tests give the same answer whatever day they're run
    private final LocalDate today = LocalDate.of(2026, 11, 1);
    private final SlowStockReport report = new SlowStockReport();

    private Item boughtDaysAgo(String name, int days) {
        return new Item(name, "M", today.minusDays(days), 20);
    }

    @Test
    void showsOldUnsoldItemsOnly() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(boughtDaysAgo("bought last week", 7));
        items.add(boughtDaysAgo("bought 90 days ago", 90));
        Item oldButSold = boughtDaysAgo("old but sold", 200);
        oldButSold.markAsSold(today.minusDays(10), 50);
        items.add(oldButSold);

        ArrayList<Item> slow = report.getSlowItems(new Inventory(items), 60, today);

        assertEquals(1, slow.size());
        assertEquals("bought 90 days ago", slow.get(0).getDescription());
    }

    @Test
    void itemExactlyOnTheLimitIsIncluded() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(boughtDaysAgo("60 days", 60));
        items.add(boughtDaysAgo("59 days", 59));

        ArrayList<Item> slow = report.getSlowItems(new Inventory(items), 60, today);

        assertEquals(1, slow.size());
        assertEquals("60 days", slow.get(0).getDescription());
    }

    @Test
    void sortsOldestFirst() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(boughtDaysAgo("middle", 80));
        list.add(boughtDaysAgo("newest", 61));
        list.add(boughtDaysAgo("oldest", 150));

        SlowStockReport.sortOldestFirst(list);

        assertEquals("oldest", list.get(0).getDescription());
        assertEquals("middle", list.get(1).getDescription());
        assertEquals("newest", list.get(2).getDescription());
    }

    @Test
    void sortingAnEmptyListDoesNothing() {
        ArrayList<Item> list = new ArrayList<>();
        SlowStockReport.sortOldestFirst(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void suggestionDependsOnHowOld() {
        assertEquals("Keep it listed", report.getSuggestion(boughtDaysAgo("x", 30), today));
        assertEquals("Retake photos and relist", report.getSuggestion(boughtDaysAgo("x", 70), today));
        assertEquals("Bundle it with a similar Other item", report.getSuggestion(boughtDaysAgo("x", 100), today));
        assertEquals("Sell at cost (£20.00) to free up the money", report.getSuggestion(boughtDaysAgo("x", 130), today));
    }

    @Test
    void moneyTiedUp() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item("a", "M", today, 20));
        list.add(new Item("b", "M", today, 35.5));
        assertEquals(55.5, report.getMoneyTiedUp(list));
    }
}
