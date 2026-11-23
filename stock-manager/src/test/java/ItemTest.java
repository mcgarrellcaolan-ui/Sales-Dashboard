import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void newItemIsInStock() {
        Item item = new Item("Stone Island jumper", "L", LocalDate.of(2026, 6, 1), 30);
        assertFalse(item.isSold());
        assertEquals(0, item.getProfit());
    }

    @Test
    void markAsSoldWorksOutProfit() {
        Item item = new Item("Stone Island jumper", "L", LocalDate.of(2026, 6, 1), 30);
        item.markAsSold(LocalDate.of(2026, 6, 10), 75);

        assertTrue(item.isSold());
        assertEquals(75, item.getSalePrice());
        assertEquals(45, item.getProfit());
    }

    @Test
    void daysInStock() {
        Item item = new Item("CP shell", "M", LocalDate.of(2026, 8, 1), 40);
        assertEquals(31, item.getDaysInStock(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void brands() {
        assertEquals("Stone Island", makeItem("Black Stone Island crewneck jumper").getBrand());
        assertEquals("Stone Island", makeItem("navy SI jumper").getBrand());
        assertEquals("CP Company", makeItem("CP Company goggle jacket").getBrand());
        assertEquals("CP Company", makeItem("C.P. Company overshirt").getBrand());
        assertEquals("Napapijri", makeItem("Napa Rainforest jacket").getBrand());
        assertEquals("Other", makeItem("Arc'teryx fleece").getBrand());
    }

    @Test
    void siInsideAWordIsNotStoneIsland() {
        assertEquals("Other", makeItem("classic tee").getBrand());
    }

    private Item makeItem(String description) {
        return new Item(description, "M", LocalDate.of(2026, 1, 1), 10);
    }
}
