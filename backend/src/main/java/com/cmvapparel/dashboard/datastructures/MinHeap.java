package com.cmvapparel.dashboard.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Min-heap stored in an ArrayList. Smallest item is always at index 0.
// Children of i are at 2i + 1 and 2i + 2, parent is at (i - 1) / 2.
// add and removeMin are O(log n).
public class MinHeap<T> {

    private final List<T> heap = new ArrayList<>();
    private final Comparator<T> order;

    public MinHeap(Comparator<T> order) {
        this.order = order;
    }

    public int size() {
        return heap.size();
    }

    public T peek() {
        return heap.get(0);
    }

    public void add(T item) {
        heap.add(item);
        siftUp(heap.size() - 1);
    }

    public T removeMin() {
        T min = heap.get(0);
        T last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return min;
    }

    // Top n items, biggest first. Keeps only n items in the heap, so O(m log n).
    public static <T> List<T> topN(List<T> items, int n, Comparator<T> order) {
        MinHeap<T> best = new MinHeap<>(order);
        for (T item : items) {
            best.add(item);
            if (best.size() > n) {
                best.removeMin();
            }
        }

        List<T> result = new ArrayList<>();
        while (best.size() > 0) {
            result.add(best.removeMin());
        }
        Collections.reverse(result);
        return result;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (order.compare(heap.get(i), heap.get(parent)) >= 0) {
                break;
            }
            Collections.swap(heap, i, parent);
            i = parent;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < heap.size() && order.compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < heap.size() && order.compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == i) {
                return;
            }
            Collections.swap(heap, i, smallest);
            i = smallest;
        }
    }
}
