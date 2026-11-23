import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory makeInventory() {
        LocalDate bought = LocalDate.of(2026, 6, 1);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("CP jacket", "L", bought, 60, LocalDate.of(2026, 7, 1), 100, 40));
        items.add(new Item("Stone Island jumper", "M", bought, 30, LocalDate.of(2026, 7, 5), 80, 50));
        items.add(new Item("Napapijri fleece", "L", bought, 25));
        items.add(new Item("Stone Island polo", "S", bought, 15));
        return new Inventory(items);
    }

    @Test
    void totals() {
        Inventory inventory = makeInventory();
        assertEquals(180, inventory.getTotalRevenue());
        assertEquals(90, inventory.getTotalProfit());
        assertEquals(2, inventory.countSold());
        assertEquals(2, inventory.countInStock());
        assertEquals(40, inventory.getStockValue());
    }

    @Test
    void searchIgnoresCapitals() {
        Inventory inventory = makeInventory();
        assertEquals(2, inventory.search("stone island").size());
        assertEquals(1, inventory.search("FLEECE").size());
        assertEquals(0, inventory.search("gucci").size());
    }

    @Test
    void markingAsSoldUpdatesTotals() {
        Inventory inventory = makeInventory();
        Item polo = inventory.search("polo").get(0);
        polo.markAsSold(LocalDate.of(2026, 8, 1), 35);

        assertEquals(3, inventory.countSold());
        assertEquals(1, inventory.countInStock());
        assertEquals(110, inventory.getTotalProfit());
    }

    @Test
    void sampleFileMatchesTheDashboard() throws Exception {
        Inventory inventory = new Inventory(CsvReader.read("../data/sample-data.csv"));
        assertEquals(1659, inventory.getTotalRevenue());
        assertEquals(20, inventory.countSold());
        assertEquals(10, inventory.countInStock());
    }
}
