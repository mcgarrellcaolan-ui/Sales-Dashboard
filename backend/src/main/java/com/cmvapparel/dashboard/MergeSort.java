package com.cmvapparel.dashboard;

import java.util.ArrayList;

// Sorts sales by the date they sold, oldest first.
// Split the list in half, sort each half, then merge them back together. O(n log n).
public class MergeSort {

    public static ArrayList<Item> sortByDate(ArrayList<Item> list) {
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

        return merge(sortByDate(left), sortByDate(right));
    }

    private static ArrayList<Item> merge(ArrayList<Item> left, ArrayList<Item> right) {
        ArrayList<Item> result = new ArrayList<>();
        int i = 0;
        int j = 0;

        // take whichever front item is older
        while (i < left.size() && j < right.size()) {
            if (!right.get(j).getSoldOn().isBefore(left.get(i).getSoldOn())) {
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
}
