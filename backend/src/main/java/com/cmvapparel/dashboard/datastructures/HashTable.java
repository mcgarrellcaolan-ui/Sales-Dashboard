package com.cmvapparel.dashboard.datastructures;

import java.util.ArrayList;
import java.util.List;

// Hash table using separate chaining (each bucket is a linked list).
// put and get are O(1) on average, O(n) worst case if everything lands in one bucket.
public class HashTable<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final double LOAD_FACTOR = 0.75;

    private Entry<K, V>[] buckets = newArray(16);
    private int size = 0;

    public void put(K key, V value) {
        int index = indexOf(key, buckets.length);

        // key already there, just update it
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }

        // add new entry to the front of the bucket
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;

        if (size > buckets.length * LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        for (Entry<K, V> e = buckets[indexOf(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return buckets.length;
    }

    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                keys.add(e.key);
            }
        }
        return keys;
    }

    // & 0x7fffffff makes the hash positive
    private int indexOf(K key, int length) {
        return (key.hashCode() & 0x7fffffff) % length;
    }

    // double the buckets and move every entry, since its index depends on the length
    private void resize() {
        Entry<K, V>[] old = buckets;
        buckets = newArray(old.length * 2);
        for (Entry<K, V> bucket : old) {
            Entry<K, V> e = bucket;
            while (e != null) {
                Entry<K, V> next = e.next;
                int index = indexOf(e.key, buckets.length);
                e.next = buckets[index];
                buckets[index] = e;
                e = next;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Entry<K, V>[] newArray(int length) {
        return (Entry<K, V>[]) new Entry<?, ?>[length];
    }
}
