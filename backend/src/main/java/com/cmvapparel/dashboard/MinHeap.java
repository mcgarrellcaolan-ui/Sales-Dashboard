package com.cmvapparel.dashboard;

import java.util.ArrayList;

// Min-heap of sales, ordered by profit. The least profitable sale is always at index 0.
// For the item at i: children are at 2i + 1 and 2i + 2, parent is at (i - 1) / 2.
public class MinHeap {

    private ArrayList<Item> heap = new ArrayList<>();

    public int size() {
        return heap.size();
    }

    public Item peek() {
        return heap.get(0);
    }

    // O(log n)
    public void add(Item item) {
        heap.add(item);

        // move it up while it's smaller than its parent
        int i = heap.size() - 1;
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (profit(i) >= profit(parent)) {
                break;
            }
            swap(i, parent);
            i = parent;
        }
    }

    // O(log n)
    public Item removeMin() {
        Item min = heap.get(0);
        Item last = heap.remove(heap.size() - 1);
        if (heap.size() == 0) {
            return min;
        }
        heap.set(0, last);

        // move it down while a child is smaller
        int i = 0;
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;
            if (left < heap.size() && profit(left) < profit(smallest)) {
                smallest = left;
            }
            if (right < heap.size() && profit(right) < profit(smallest)) {
                smallest = right;
            }
            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }
        return min;
    }

    // The n most profitable sales, best first.
    // The heap never holds more than n items, so this is O(m log n) for m sales.
    public static ArrayList<Item> topN(ArrayList<Item> sales, int n) {
        MinHeap heap = new MinHeap();
        for (Item sale : sales) {
            heap.add(sale);
            if (heap.size() > n) {
                heap.removeMin(); // throw away the worst one
            }
        }

        // removeMin gives them smallest first, so add each one to the front
        ArrayList<Item> result = new ArrayList<>();
        while (heap.size() > 0) {
            result.add(0, heap.removeMin());
        }
        return result;
    }

    private double profit(int i) {
        return heap.get(i).getProfit();
    }

    private void swap(int a, int b) {
        Item temp = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, temp);
    }
}
