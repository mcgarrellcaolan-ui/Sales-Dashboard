package com.cmvapparel.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;

// Sorts items by date, oldest first.
// Split the list in half, sort each half, then merge them back together. O(n log n).
// The same code sorts by the date sold or the date bought.
public class MergeSort {

    public static ArrayList<Item> sortBySoldDate(ArrayList<Item> list) {
        return sort(list, false);
    }

    public static ArrayList<Item> sortByBoughtDate(ArrayList<Item> list) {
        return sort(list, true);
    }

    private static ArrayList<Item> sort(ArrayList<Item> list, boolean useBoughtDate) {
        if (list.size() <= 1) {
            return list;
        }

        int middle = list.size() / 2;
        ArrayList<Item> left = new ArrayList<>();
        ArrayList<Item> right = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i < middle) {
                left.add(list.get(i));
            } else {
                right.add(list.get(i));
            }
        }

        return merge(sort(left, useBoughtDate), sort(right, useBoughtDate), useBoughtDate);
    }

    private static ArrayList<Item> merge(ArrayList<Item> left, ArrayList<Item> right, boolean useBoughtDate) {
        ArrayList<Item> result = new ArrayList<>();
        int i = 0;
        int j = 0;

        // take whichever front item is older
        while (i < left.size() && j < right.size()) {
            LocalDate leftDate = dateOf(left.get(i), useBoughtDate);
            LocalDate rightDate = dateOf(right.get(j), useBoughtDate);
            if (!rightDate.isBefore(leftDate)) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        // add whatever is left over
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }
        return result;
    }

    private static LocalDate dateOf(Item item, boolean useBoughtDate) {
        if (useBoughtDate) {
            return item.getBoughtOn();
        }
        return item.getSoldOn();
    }
}
