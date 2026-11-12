package com.cmvapparel.dashboard.io;

import com.cmvapparel.dashboard.model.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Reads sales.csv
// columns: description,size,boughtOn,cost,soldOn,price,profit
public class CsvLoader {

    public static List<Item> load(String path) throws IOException {
        List<Item> items = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    items.add(parseLine(line));
                }
            }
        }
        return items;
    }

    static Item parseLine(String line) {
        String[] cells = line.split(",", -1); // -1 so empty cells at the end aren't dropped
        return new Item(cells[0], cells[1], toDate(cells[2]), toNumber(cells[3]),
                toDate(cells[4]), toNumber(cells[5]), toNumber(cells[6]));
    }

    private static LocalDate toDate(String text) {
        if (text.isBlank()) {
            return null;
        }
        return LocalDate.parse(text);
    }

    private static double toNumber(String text) {
        if (text.isBlank()) {
            return 0;
        }
        return Double.parseDouble(text);
    }
}
