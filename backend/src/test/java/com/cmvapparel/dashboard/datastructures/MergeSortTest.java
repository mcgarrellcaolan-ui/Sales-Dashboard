package com.cmvapparel.dashboard.datastructures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    @Test
    void sortsNumbers() {
        assertEquals(List.of(1, 3, 3, 5, 7, 9), MergeSort.sort(List.of(5, 3, 9, 1, 3, 7), Comparator.<Integer>naturalOrder()));
    }

    @Test
    void emptyAndOneItem() {
        assertEquals(List.of(), MergeSort.sort(new ArrayList<Integer>(), Comparator.<Integer>naturalOrder()));
        assertEquals(List.of(4), MergeSort.sort(List.of(4), Comparator.<Integer>naturalOrder()));
    }

    @Test
    void isStable() {
        // sorted by length, "bb" was before "aa" so it should stay before it
        List<String> sorted = MergeSort.sort(List.of("ccc", "bb", "a", "aa"), Comparator.comparingInt(String::length));
        assertEquals(List.of("a", "bb", "aa", "ccc"), sorted);
    }

    @Test
    void sameAsJavaSort() {
        Random random = new Random(42);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            list.add(random.nextInt(1000));
        }
        List<Integer> expected = new ArrayList<>(list);
        expected.sort(null);
        assertEquals(expected, MergeSort.sort(list, Comparator.<Integer>naturalOrder()));
    }
}
