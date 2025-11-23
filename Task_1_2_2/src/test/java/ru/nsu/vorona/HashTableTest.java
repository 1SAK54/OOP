package ru.nsu.vorona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

/**
 * Тесты для HashTable.
 */
class HashTableTest {

    @Test
    void testEntryGetKey() {
        HashTable.Entry<String, Integer> entry = new HashTable.Entry<>("key", 42);
        assertEquals("key", entry.getKey());
    }

    @Test
    void testEntrySetValue() {
        HashTable.Entry<String, Integer> entry = new HashTable.Entry<>("key", 42);
        entry.setValue(100);
        assertEquals(100, entry.getValue());
    }

    @Test
    void testCreateEmptyHashTable() {
        HashTable<String, Integer> table = new HashTable<>();

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }

    @Test
    void testPut() {
        HashTable<String, Integer> table = new HashTable<>();

        assertTrue(table.put("one", 1));
        assertTrue(table.put("two", 2));
        assertFalse(table.put("one", 10));

        assertEquals(2, table.size());
    }

    @Test
    void testGet() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);

        assertEquals(1, table.get("one"));
        assertEquals(2, table.get("two"));
        assertNull(table.get("three"));
    }

    @Test
    void testUpdate() {
        HashTable<String, Number> table = new HashTable<>();
        table.put("one", 1);

        assertTrue(table.update("one", 1.0));
        assertEquals(1.0, table.get("one"));

        assertFalse(table.update("two", 2.0));
    }

    @Test
    void testRemove() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);

        assertEquals(1, table.remove("one"));
        assertNull(table.get("one"));
        assertEquals(1, table.size());

        assertNull(table.remove("three"));
    }

    @Test
    void testContainsKey() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);

        assertTrue(table.containsKey("one"));
        assertFalse(table.containsKey("two"));
    }

    @Test
    void testClear() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);

        table.clear();

        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }

    @Test
    void testIteration() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);
        table.put("three", 3);

        int count = 0;
        for (HashTable.Entry<String, Integer> entry : table) {
            assertTrue(entry.getValue() >= 1 && entry.getValue() <= 3);
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    void testConcurrentModificationException() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        table.put("two", 2);

        assertThrows(ConcurrentModificationException.class, () -> {
            for (HashTable.Entry<String, Integer> entry : table) {
                table.put("three", 3);
            }
        });
    }

    @Test
    void testEquals() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        table1.put("one", 1);
        table1.put("two", 2);

        table2.put("one", 1);
        table2.put("two", 2);

        assertEquals(table1, table2);
    }

    @Test
    void testNotEquals() {
        HashTable<String, Integer> table1 = new HashTable<>();
        HashTable<String, Integer> table2 = new HashTable<>();

        table1.put("one", 1);
        table2.put("one", 2);

        assertNotEquals(table1, table2);
    }

    @Test
    void testToString() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);

        String str = table.toString();
        assertTrue(str.contains("one=1"));
    }

    @Test
    void testNullKey() {
        HashTable<String, Integer> table = new HashTable<>();

        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
    }

    @Test
    void testResize() {
        HashTable<Integer, String> table = new HashTable<>(4);

        for (int i = 0; i < 20; i++) {
            table.put(i, "value" + i);
        }

        assertEquals(20, table.size());
        for (int i = 0; i < 20; i++) {
            assertEquals("value" + i, table.get(i));
        }
    }

    @Test
    void testCollisions() {
        HashTable<String, Integer> table = new HashTable<>(4);

        table.put("a", 1);
        table.put("b", 2);
        table.put("c", 3);

        assertEquals(1, table.get("a"));
        assertEquals(2, table.get("b"));
        assertEquals(3, table.get("c"));
    }

    @Test
    void testExampleFromTask() {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);

        assertEquals(1.0, hashTable.get("one"));
    }
}