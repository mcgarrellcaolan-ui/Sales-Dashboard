package com.cmvapparel.dashboard.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void putAndGet() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("Stone Island", 9);
        table.put("CP Company", 6);
        assertEquals(9, (int) table.get("Stone Island"));
        assertEquals(6, (int) table.get("CP Company"));
        assertNull(table.get("Napapijri"));
        assertEquals(2, table.size());
    }

    @Test
    void sameKeyUpdatesValue() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("a", 1);
        table.put("a", 2);
        assertEquals(2, (int) table.get("a"));
        assertEquals(1, table.size());
    }

    @Test
    void resizesAndKeepsEverything() {
        HashTable<Integer, Integer> table = new HashTable<>();
        for (int i = 0; i < 1000; i++) {
            table.put(i, i * i);
        }
        assertEquals(1000, table.size());
        assertTrue(table.capacity() > 1000);
        for (int i = 0; i < 1000; i++) {
            assertEquals(i * i, (int) table.get(i));
        }
    }

    @Test
    void collisions() {
        // "Aa" and "BB" have the same hashCode
        HashTable<String, String> table = new HashTable<>();
        table.put("Aa", "first");
        table.put("BB", "second");
        assertEquals("first", table.get("Aa"));
        assertEquals("second", table.get("BB"));
        assertEquals(2, table.keys().size());
    }
}
