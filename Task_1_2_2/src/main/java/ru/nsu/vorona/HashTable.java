package ru.nsu.vorona;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Параметризованная хеш-таблица с методом цепочек для разрешения коллизий.
 * Поддерживает динамическое изменение размера и итерирование.
 *
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class HashTable<K, V> implements Iterable<HashTable.Entry<K, V>> {

    /**
     * Класс для хранения пары ключ-значение.
     *
     * @param <K> тип ключа
     * @param <V> тип значения
     */
    public static class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;

        /**
         * Создаёт новую запись.
         *
         * @param key ключ
         * @param value значение
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        /**
         * Возвращает ключ.
         *
         * @return ключ
         */
        public K getKey() {
            return key;
        }

        /**
         * Возвращает значение.
         *
         * @return значение
         */
        public V getValue() {
            return value;
        }

        /**
         * Устанавливает новое значение.
         *
         * @param value новое значение
         */
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry<?, ?> other = (Entry<?, ?>) obj;
            return Objects.equals(key, other.key) && Objects.equals(value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private int modCount;

    /**
     * Создаёт пустую хеш-таблицу с начальной ёмкостью.
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Создаёт пустую хеш-таблицу с заданной ёмкостью.
     *
     * @param initialCapacity начальная ёмкость
     */
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.table = (Entry<K, V>[]) new Entry[initialCapacity];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Добавляет пару ключ-значение в таблицу.
     * Если ключ уже существует, значение НЕ перезаписывается.
     *
     * @param key ключ
     * @param value значение
     * @return true, если пара была добавлена; false, если ключ уже существует
     */
    public boolean put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return false;
            }
            current = current.next;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
        modCount++;
        return true;
    }

    /**
     * Обновляет значение по ключу.
     *
     * @param key ключ
     * @param value новое значение
     * @return true, если значение было обновлено; false, если ключ не найден
     */
    public boolean update(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = getIndex(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                modCount++;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key ключ
     * @return значение или null, если ключ не найден
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = getIndex(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    /**
     * Удаляет пару ключ-значение по ключу.
     *
     * @param key ключ
     * @return значение удалённого элемента или null, если ключ не найден
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = getIndex(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                modCount++;
                return current.value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    /**
     * Проверяет наличие ключа в таблице.
     *
     * @param key ключ
     * @return true, если ключ существует
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Возвращает количество элементов в таблице.
     *
     * @return размер таблицы
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет, пуста ли таблица.
     *
     * @return true, если таблица пуста
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Очищает таблицу.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        size = 0;
        modCount++;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HashTable)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        HashTable other = (HashTable) obj;

        if (size != other.size()) {
            return false;
        }

        for (Entry<K, V> entry : this) {
            @SuppressWarnings("unchecked")
            Object otherValue = other.get(entry.key);

            if (!Objects.equals(entry.value, otherValue)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        for (Entry<K, V> entry : this) {
            hash += entry.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Entry<K, V> entry : this) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.key).append("=").append(entry.value);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Вычисляет индекс для ключа.
     */
    private int getIndex(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    /**
     * Увеличивает размер таблицы и перехеширует элементы.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[oldTable.length * 2];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            Entry<K, V> current = entry;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    /**
     * Итератор для обхода элементов хеш-таблицы.
     */
    private class HashTableIterator implements Iterator<Entry<K, V>> {
        private int currentBucket;
        private Entry<K, V> currentEntry;
        private final int expectedModCount;

        HashTableIterator() {
            this.currentBucket = 0;
            this.currentEntry = null;
            this.expectedModCount = modCount;
            findNextEntry();
        }

        @Override
        public boolean hasNext() {
            return currentEntry != null;
        }

        @Override
        public Entry<K, V> next() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<K, V> result = currentEntry;
            currentEntry = currentEntry.next;
            if (currentEntry == null) {
                currentBucket++;
                findNextEntry();
            }

            return result;
        }

        private void findNextEntry() {
            while (currentBucket < table.length && table[currentBucket] == null) {
                currentBucket++;
            }
            if (currentBucket < table.length) {
                currentEntry = table[currentBucket];
            } else {
                currentEntry = null;
            }
        }
    }
}