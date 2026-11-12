package com.cmvapparel.dashboard.io;

import com.cmvapparel.dashboard.model.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

// Reads the "Inventory Detail" sheet from the same Excel file the dashboard uses.
// An .xlsx file is a zip of XML files, so this unzips it and reads the XML.
//
// Columns: B description, C size, D date bought, E cost, F date sold, G price, J profit
public class ExcelLoader {

    private static final String SHEET = "Inventory Detail";

    public static List<Item> load(String path) throws Exception {
        try (ZipFile zip = new ZipFile(path)) {
            List<String> sharedText = readSharedText(zip);
            Document sheet = readXml(zip, findSheetFile(zip));

            List<Item> items = new ArrayList<>();
            NodeList rows = sheet.getElementsByTagName("row");
            for (int i = 0; i < rows.getLength(); i++) {
                Map<String, String> row = readRow((Element) rows.item(i), sharedText);
                // only rows with a description and a number for the cost are items
                // (this skips the title and header rows)
                if (!row.getOrDefault("B", "").isBlank() && isNumber(row.get("E"))) {
                    items.add(new Item(
                            row.get("B"),
                            row.getOrDefault("C", ""),
                            toDate(row.get("D")),
                            toNumber(row.get("E")),
                            toDate(row.get("F")),
                            toNumber(row.get("G")),
                            toNumber(row.get("J"))));
                }
            }
            return items;
        }
    }

    // Gets each cell in a row as text, e.g. {"B": "CP Company jacket", "E": "42"}
    private static Map<String, String> readRow(Element row, List<String> sharedText) {
        Map<String, String> cells = new HashMap<>();
        NodeList cellList = row.getElementsByTagName("c");
        for (int i = 0; i < cellList.getLength(); i++) {
            Element cell = (Element) cellList.item(i);
            String column = cell.getAttribute("r").replaceAll("[0-9]", ""); // "B4" -> "B"
            String value = valueOf(cell);

            // "s" means the text is stored once in sharedStrings.xml and this is its index
            if (cell.getAttribute("t").equals("s")) {
                value = sharedText.get(Integer.parseInt(value));
            }
            cells.put(column, value.trim());
        }
        return cells;
    }

    // the value is in <v>, or in <is><t> for text typed straight into the cell
    private static String valueOf(Element cell) {
        NodeList v = cell.getElementsByTagName("v");
        if (v.getLength() > 0) {
            return v.item(0).getTextContent();
        }
        NodeList t = cell.getElementsByTagName("t");
        return t.getLength() > 0 ? t.item(0).getTextContent() : "";
    }

    // Excel stores repeated text once in sharedStrings.xml
    private static List<String> readSharedText(ZipFile zip) throws Exception {
        List<String> text = new ArrayList<>();
        if (zip.getEntry("xl/sharedStrings.xml") == null) {
            return text;
        }
        NodeList items = readXml(zip, "xl/sharedStrings.xml").getElementsByTagName("si");
        for (int i = 0; i < items.getLength(); i++) {
            text.add(items.item(i).getTextContent());
        }
        return text;
    }

    // workbook.xml gives the sheet's id, and workbook.xml.rels says which file that id is
    private static String findSheetFile(ZipFile zip) throws Exception {
        String id = null;
        NodeList sheets = readXml(zip, "xl/workbook.xml").getElementsByTagName("sheet");
        for (int i = 0; i < sheets.getLength(); i++) {
            Element sheet = (Element) sheets.item(i);
            if (sheet.getAttribute("name").equals(SHEET)) {
                id = sheet.getAttribute("r:id");
            }
        }
        if (id == null) {
            throw new IllegalArgumentException("No sheet called " + SHEET);
        }

        NodeList links = readXml(zip, "xl/_rels/workbook.xml.rels").getElementsByTagName("Relationship");
        for (int i = 0; i < links.getLength(); i++) {
            Element link = (Element) links.item(i);
            if (link.getAttribute("Id").equals(id)) {
                String target = link.getAttribute("Target");
                // the path can be "worksheets/sheet1.xml" or "/xl/worksheets/sheet1.xml"
                return target.startsWith("/") ? target.substring(1) : "xl/" + target;
            }
        }
        throw new IllegalArgumentException("Can't find the file for " + SHEET);
    }

    private static Document readXml(ZipFile zip, String file) throws Exception {
        try (InputStream in = zip.getInputStream(zip.getEntry(file))) {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }
    }

    private static boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static double toNumber(String text) {
        return isNumber(text) ? Double.parseDouble(text) : 0;
    }

    // Excel stores dates as the number of days since 30 Dec 1899
    static LocalDate toDate(String text) {
        if (!isNumber(text) || Double.parseDouble(text) <= 0) {
            return null;
        }
        return LocalDate.of(1899, 12, 30).plusDays((long) Double.parseDouble(text));
    }
}
