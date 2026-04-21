package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты курьера
 */
class CourierTest {

    /**
     * Проверяет, что курьер забирает заказ со склада и доставляет его
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldDeliverOrderFromStorage() throws InterruptedException {
        Storage storage = new Storage(2);

        Order order = new Order(1);
        order.setStatus(OrderStatus.IN_STORAGE);
        storage.put(order);
        storage.finishProduction();

        Courier courier = new Courier(1, 1, 10, storage);
        courier.start();
        courier.join();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    /**
     * Проверяет, что курьер может доставить несколько заказов за один раз
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldDeliverBatchOfOrders() throws InterruptedException {
        Storage storage = new Storage(5);

        Order order1 = new Order(1);
        Order order2 = new Order(2);

        order1.setStatus(OrderStatus.IN_STORAGE);
        order2.setStatus(OrderStatus.IN_STORAGE);

        storage.put(order1);
        storage.put(order2);
        storage.finishProduction();

        Courier courier = new Courier(1, 2, 10, storage);
        courier.start();
        courier.join();

        assertEquals(OrderStatus.DELIVERED, order1.getStatus());
        assertEquals(OrderStatus.DELIVERED, order2.getStatus());
    }
}