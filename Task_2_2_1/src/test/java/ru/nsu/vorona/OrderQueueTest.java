package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты очереди заказов
 */
class OrderQueueTest {

    /**
     * Проверяет добавление и получение заказа
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldAddAndTakeOrder() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        Order order = new Order(1);

        boolean added = queue.add(order);
        Order taken = queue.take();

        assertTrue(added);
        assertEquals(order, taken);
    }

    /**
     * Проверяет, что после закрытия пустая очередь возвращает null
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldReturnNullWhenClosedAndEmpty() throws InterruptedException {
        OrderQueue queue = new OrderQueue();

        queue.close();
        Order taken = queue.take();

        assertNull(taken);
    }

    /**
     * Проверяет флаг закрытой пустой очереди
     */
    @Test
    void shouldDetectClosedAndEmptyQueue() {
        OrderQueue queue = new OrderQueue();

        assertFalse(queue.isClosedAndEmpty());

        queue.close();

        assertTrue(queue.isClosedAndEmpty());
    }

    /**
     * Проверяет, что после закрытия нельзя добавить заказ
     */
    @Test
    void shouldNotAddOrderAfterClose() {
        OrderQueue queue = new OrderQueue();

        queue.close();
        boolean added = queue.add(new Order(2));

        assertFalse(added);
    }
}