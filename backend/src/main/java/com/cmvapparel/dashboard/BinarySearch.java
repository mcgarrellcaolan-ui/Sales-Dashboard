package com.cmvapparel.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;

// Binary search on sales that are already sorted by date. O(log n).
public class BinarySearch {

    // position of the first sale on or after the date (or sales.size() if there isn't one)
    public static int firstOnOrAfter(ArrayList<Item> sales, LocalDate date) {
        int low = 0;
        int high = sales.size();

        while (low < high) {
            int middle = (low + high) / 2;
            if (sales.get(middle).getSoldOn().isBefore(date)) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low;
    }
}
