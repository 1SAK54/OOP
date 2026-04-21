package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты склада
 */
class StorageTest {

    /**
     * Проверяет добавление и получение партии заказов
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldPutAndTakeBatch() throws InterruptedException {
        Storage storage = new Storage(3);
        Order order1 = new Order(1);
        Order order2 = new Order(2);

        storage.put(order1);
        storage.put(order2);

        List<Order> batch = storage.takeBatch(2);

        assertEquals(2, batch.size());
        assertTrue(batch.contains(order1));
        assertTrue(batch.contains(order2));
    }

    /**
     * Проверяет, что после завершения производства пустой склад возвращает пустой список
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldReturnEmptyListWhenFinishedAndEmpty() throws InterruptedException {
        Storage storage = new Storage(2);

        storage.finishProduction();
        List<Order> batch = storage.takeBatch(2);

        assertTrue(batch.isEmpty());
    }

    /**
     * Проверяет, что можно забрать меньше заказов, чем есть на складе
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldTakeOnlyRequestedCount() throws InterruptedException {
        Storage storage = new Storage(5);

        storage.put(new Order(1));
        storage.put(new Order(2));
        storage.put(new Order(3));

        List<Order> batch = storage.takeBatch(2);

        assertEquals(2, batch.size());
    }
}