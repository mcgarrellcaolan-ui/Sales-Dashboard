import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

// Reads my stock from a CSV file.
// To make the CSV: open the spreadsheet, go to the "Inventory Detail" sheet,
// then File > Save As > CSV.
//
// Columns: A number, B description, C size, D date bought, E cost,
//          F date sold, G sale price, H postage, I fees, J profit
public class CsvReader {

    // Excel saves the dates the way they look in the cell, e.g. 01 Jun 2026
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    public static ArrayList<Item> read(String fileName) throws IOException {
        ArrayList<Item> items = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line = reader.readLine();
        while (line != null) {
            Item item = makeItem(line);
            if (item != null) {
                items.add(item);
            }
            line = reader.readLine();
        }

        reader.close();
        return items;
    }

    // Turns one line into an Item.
    // Returns null if the line isn't an item, e.g. the title, the headers or a blank line.
    // (Item descriptions can't have commas in them, because a comma splits the columns.)
    private static Item makeItem(String line) {
        String[] parts = line.split(",", -1); // -1 keeps empty columns at the end
        if (parts.length < 10) {
            return null;
        }

        try {
            String description = parts[1].trim();
            String size = parts[2].trim();
            LocalDate dateBought = LocalDate.parse(parts[3].trim(), DATE_FORMAT);
            double cost = Double.parseDouble(parts[4].trim());

            // no sale date means it's still in stock
            if (parts[5].isBlank()) {
                return new Item(description, size, dateBought, cost);
            }

            LocalDate dateSold = LocalDate.parse(parts[5].trim(), DATE_FORMAT);
            double salePrice = Double.parseDouble(parts[6].trim());
            double profit = Double.parseDouble(parts[9].trim());
            return new Item(description, size, dateBought, cost, dateSold, salePrice, profit);

        } catch (Exception e) {
            return null; // a date or number didn't read properly, so it isn't an item row
        }
    }
}
