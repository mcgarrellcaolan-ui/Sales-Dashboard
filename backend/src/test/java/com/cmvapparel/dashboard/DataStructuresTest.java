package com.cmvapparel.dashboard;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataStructuresTest {

    private Item sale(String name, int julyDay, double profit) {
        return new Item(name, LocalDate.of(2026, 6, 1), 50, LocalDate.of(2026, 7, julyDay), 100, profit);
    }

    @Test
    void hashTablePutAndGet() {
        HashTable table = new HashTable();
        table.put("Stone Island", new BrandTotal("Stone Island"));
        table.put("CP Company", new BrandTotal("CP Company"));

        assertEquals("Stone Island", table.get("Stone Island").brand);
        assertEquals("CP Company", table.get("CP Company").brand);
        assertNull(table.get("Napapijri"));
        assertEquals(2, table.size());
    }

    @Test
    void hashTableSameKeyIsUpdated() {
        HashTable table = new HashTable();
        table.put("a", new BrandTotal("first"));
        table.put("a", new BrandTotal("second"));
        assertEquals("second", table.get("a").brand);
        assertEquals(1, table.size());
    }

    @Test
    void hashTableResizes() {
        HashTable table = new HashTable();
        for (int i = 0; i < 100; i++) {
            table.put("brand" + i, new BrandTotal("brand" + i));
        }
        assertEquals(100, table.size());
        assertTrue(table.capacity() > 100);
        for (int i = 0; i < 100; i++) {
            assertEquals("brand" + i, table.get("brand" + i).brand);
        }
    }

    @Test
    void hashTableCollisions() {
        // "Aa" and "BB" have the same hashCode so they go in the same bucket
        HashTable table = new HashTable();
        table.put("Aa", new BrandTotal("first"));
        table.put("BB", new BrandTotal("second"));
        assertEquals("first", table.get("Aa").brand);
        assertEquals("second", table.get("BB").brand);
    }

    @Test
    void mergeSortByDate() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(sale("c", 20, 0));
        list.add(sale("a", 1, 0));
        list.add(sale("b", 9, 0));
        list.add(sale("b2", 9, 0));

        ArrayList<Item> sorted = MergeSort.sortBySoldDate(list);
        assertEquals("a", sorted.get(0).getDescription());
        assertEquals("b", sorted.get(1).getDescription());
        assertEquals("b2", sorted.get(2).getDescription()); // same date stays in the same order
        assertEquals("c", sorted.get(3).getDescription());
        assertEquals(0, MergeSort.sortBySoldDate(new ArrayList<>()).size());
    }

    @Test
    void binarySearch() {
        // sales on 1, 3, 3, 7 and 9 July
        ArrayList<Item> sales = new ArrayList<>();
        sales.add(sale("x", 1, 0));
        sales.add(sale("x", 3, 0));
        sales.add(sale("x", 3, 0));
        sales.add(sale("x", 7, 0));
        sales.add(sale("x", 9, 0));

        assertEquals(0, BinarySearch.firstOnOrAfter(sales, LocalDate.of(2026, 7, 1)));
        assertEquals(1, BinarySearch.firstOnOrAfter(sales, LocalDate.of(2026, 7, 3)));
        assertEquals(3, BinarySearch.firstOnOrAfter(sales, LocalDate.of(2026, 7, 5))); // no sale that day
        assertEquals(5, BinarySearch.firstOnOrAfter(sales, LocalDate.of(2026, 7, 20))); // after the last sale
    }

    @Test
    void minHeapRemovesSmallestFirst() {
        MinHeap heap = new MinHeap();
        heap.add(sale("five", 1, 5));
        heap.add(sale("two", 1, 2));
        heap.add(sale("eight", 1, 8));
        heap.add(sale("one", 1, 1));

        assertEquals("one", heap.peek().getDescription());
        assertEquals("one", heap.removeMin().getDescription());
        assertEquals("two", heap.removeMin().getDescription());
        assertEquals("five", heap.removeMin().getDescription());
        assertEquals("eight", heap.removeMin().getDescription());
    }

    @Test
    void minHeapTopN() {
        ArrayList<Item> sales = new ArrayList<>();
        sales.add(sale("4", 1, 4));
        sales.add(sale("10", 1, 10));
        sales.add(sale("1", 1, 1));
        sales.add(sale("7", 1, 7));
        sales.add(sale("9", 1, 9));

        ArrayList<Item> top = MinHeap.topN(sales, 3);
        assertEquals(3, top.size());
        assertEquals("10", top.get(0).getDescription());
        assertEquals("9", top.get(1).getDescription());
        assertEquals("7", top.get(2).getDescription());
    }
}
