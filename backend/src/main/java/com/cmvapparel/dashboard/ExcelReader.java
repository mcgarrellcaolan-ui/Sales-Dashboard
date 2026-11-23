package com.cmvapparel.dashboard;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

// Reads the "Inventory Detail" sheet using Apache POI.
// Columns: B description, D date bought, E cost, F date sold, G sale price, J profit
public class ExcelReader {

    public static ArrayList<Item> read(String file) throws Exception {
        ArrayList<Item> items = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(new File(file), null, true); // true = read only
        Sheet sheet = workbook.getSheet("Inventory Detail");

        for (Row row : sheet) {
            Cell description = row.getCell(1); // B
            Cell cost = row.getCell(4);        // E

            // skip the title, headers and blank rows
            if (description == null || description.getCellType() != CellType.STRING) {
                continue;
            }
            if (cost == null || cost.getCellType() != CellType.NUMERIC) {
                continue;
            }

            items.add(new Item(
                    description.getStringCellValue().trim(),
                    getDate(row.getCell(3)),
                    getNumber(cost),
                    getDate(row.getCell(5)),
                    getNumber(row.getCell(6)),
                    getNumber(row.getCell(9))));
        }

        workbook.close();
        return items;
    }

    private static double getNumber(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return 0;
        }
        return cell.getNumericCellValue();
    }

    private static LocalDate getDate(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return cell.getLocalDateTimeCellValue().toLocalDate();
    }
}
