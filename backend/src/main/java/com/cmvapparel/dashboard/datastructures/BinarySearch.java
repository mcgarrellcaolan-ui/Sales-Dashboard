package com.cmvapparel.dashboard.datastructures;

import com.cmvapparel.dashboard.model.Item;

import java.time.LocalDate;
import java.util.List;

// Binary search on sales sorted by date. O(log n).
public class BinarySearch {

    // index of the first sale on or after the date (sales.size() if there isn't one)
    public static int firstOnOrAfter(List<Item> sales, LocalDate date) {
        int low = 0;
        int high = sales.size();

        while (low < high) {
            int middle = low + (high - low) / 2;
            if (sales.get(middle).soldOn().isBefore(date)) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low;
    }
}
