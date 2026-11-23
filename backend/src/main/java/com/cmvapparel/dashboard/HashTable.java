package com.cmvapparel.dashboard;

import java.util.ArrayList;

// Hash table from brand name to its totals.
// Each bucket is a linked list, so two keys in the same bucket are both kept (chaining).
// put and get are O(1) on average.
public class HashTable {

    private class Node {
        String key;
        BrandTotal value;
        Node next;
    }

    private Node[] buckets = new Node[16];
    private int size = 0;

    public void put(String key, BrandTotal value) {
        int index = indexOf(key, buckets.length);

        // if the key is already there, update it
        Node current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // otherwise add a new node to the front of the bucket
        Node node = new Node();
        node.key = key;
        node.value = value;
        node.next = buckets[index];
        buckets[index] = node;
        size++;

        // too full, so double the size
        if (size > buckets.length * 0.75) {
            resize();
        }
    }

    public BrandTotal get(String key) {
        Node current = buckets[indexOf(key, buckets.length)];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return buckets.length;
    }

    public ArrayList<BrandTotal> values() {
        ArrayList<BrandTotal> list = new ArrayList<>();
        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];
            while (current != null) {
                list.add(current.value);
                current = current.next;
            }
        }
        return list;
    }

    // Math.abs because hashCode can be negative
    private int indexOf(String key, int length) {
        return Math.abs(key.hashCode() % length);
    }

    // every key goes into the new array again, because its index depends on the length
    private void resize() {
        Node[] old = buckets;
        buckets = new Node[old.length * 2];
        size = 0;
        for (int i = 0; i < old.length; i++) {
            Node current = old[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
}
