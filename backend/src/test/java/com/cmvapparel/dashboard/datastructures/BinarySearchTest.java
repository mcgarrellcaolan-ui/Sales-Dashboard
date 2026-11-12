package com.cmvapparel.dashboard.datastructures;

import com.cmvapparel.dashboard.model.Item;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTest {

    // sales on 1, 3, 3, 7 and 9 July
    private List<Item> sales() {
        List<Item> sales = new ArrayList<>();
        for (int day : new int[]{1, 3, 3, 7, 9}) {
            sales.add(new Item("item", "M", null, 10, LocalDate.of(2026, 7, day), 20, 10));
        }
        return sales;
    }

    private int search(int day) {
        return BinarySearch.firstOnOrAfter(sales(), LocalDate.of(2026, 7, day));
    }

    @Test
    void findsFirstSaleOnDate() {
        assertEquals(0, search(1));
        assertEquals(1, search(3));
        assertEquals(4, search(9));
    }

    @Test
    void noSalesOnDateGivesNextSale() {
        assertEquals(3, search(5));
    }

    @Test
    void afterLastSaleAndEmptyList() {
        assertEquals(5, search(20));
        assertEquals(0, BinarySearch.firstOnOrAfter(new ArrayList<>(), LocalDate.of(2026, 7, 1)));
    }
}
