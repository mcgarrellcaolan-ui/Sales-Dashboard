package com.cmvapparel.dashboard.datastructures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Merge sort: split in half, sort each half, merge them back together.
// O(n log n) in every case, and stable.
public class MergeSort {

    public static <T> List<T> sort(List<T> list, Comparator<T> order) {
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }

        int middle = list.size() / 2;
        List<T> left = sort(list.subList(0, middle), order);
        List<T> right = sort(list.subList(middle, list.size()), order);
        return merge(left, right, order);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> order) {
        List<T> result = new ArrayList<>();
        int i = 0;
        int j = 0;

        // take the smaller front item each time (<= keeps it stable)
        while (i < left.size() && j < right.size()) {
            if (order.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        // copy whatever is left over
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
