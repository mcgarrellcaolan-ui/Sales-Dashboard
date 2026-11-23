import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {

    @Test
    void readsTheSampleFile() throws Exception {
        ArrayList<Item> items = CsvReader.read("../data/sample-data.csv");

        // the title and header rows are skipped, leaving the 30 items
        assertEquals(30, items.size());

        Item first = items.get(0);
        assertEquals("Black Stone Island crewneck jumper", first.getDescription());
        assertEquals(LocalDate.of(2026, 6, 1), first.getDateBought());
        assertEquals(28.5, first.getCost());
        assertEquals(LocalDate.of(2026, 6, 7), first.getDateSold());
        assertEquals(65, first.getSalePrice());
        assertEquals(36.5, first.getProfit());

        Item last = items.get(29);
        assertEquals("Stone Island crinkle reps jacket", last.getDescription());
        assertFalse(last.isSold());
    }
}
