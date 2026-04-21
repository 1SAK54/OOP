package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты заказа
 */
class OrderTest {

    /**
     * Проверяет создание заказа
     */
    @Test
    void shouldCreateOrderWithId() {
        Order order = new Order(10);

        assertEquals(10, order.getId());
        assertNull(order.getStatus());
    }

    /**
     * Проверяет изменение статуса заказа
     */
    @Test
    void shouldSetAndGetStatus() {
        Order order = new Order(5);

        order.setStatus(OrderStatus.BAKING);

        assertEquals(OrderStatus.BAKING, order.getStatus());
    }
}