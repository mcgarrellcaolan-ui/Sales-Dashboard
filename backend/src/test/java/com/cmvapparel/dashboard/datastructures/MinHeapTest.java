package com.cmvapparel.dashboard.datastructures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    @Test
    void removeMinGivesSmallest() {
        MinHeap<Integer> heap = new MinHeap<>(Comparator.<Integer>naturalOrder());
        for (int n : new int[]{5, 2, 8, 1, 9, 3}) {
            heap.add(n);
        }
        assertEquals(1, (int) heap.peek());
        List<Integer> out = new ArrayList<>();
        while (heap.size() > 0) {
            out.add(heap.removeMin());
        }
        assertEquals(List.of(1, 2, 3, 5, 8, 9), out);
    }

    @Test
    void topN() {
        assertEquals(List.of(10, 9, 7), MinHeap.topN(List.of(4, 10, 1, 7, 3, 9), 3, Comparator.<Integer>naturalOrder()));
        assertEquals(List.of(2, 1), MinHeap.topN(List.of(1, 2), 5, Comparator.<Integer>naturalOrder()));
    }

    @Test
    void topNSameAsSorting() {
        Random random = new Random(7);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            list.add(random.nextInt(10000));
        }
        List<Integer> expected = new ArrayList<>(list);
        expected.sort(Comparator.reverseOrder());
        assertEquals(expected.subList(0, 10), MinHeap.topN(list, 10, Comparator.<Integer>naturalOrder()));
    }
}
