package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты пекаря
 */
class BakerTest {

    /**
     * Проверяет, что пекарь берёт заказ из очереди и кладёт его на склад
     *
     * @throws InterruptedException если поток прерван
     */
    @Test
    void shouldBakeOrderAndPutItToStorage() throws InterruptedException {
        OrderQueue orderQueue = new OrderQueue();
        Storage storage = new Storage(2);

        Order order = new Order(1);
        order.setStatus(OrderStatus.IN_QUEUE);
        orderQueue.add(order);
        orderQueue.close();

        Baker baker = new Baker(1, 10, orderQueue, storage);
        baker.start();
        baker.join();

        storage.finishProduction();
        List<Order> batch = storage.takeBatch(1);

        assertEquals(1, batch.size());
        assertEquals(order, batch.get(0));
        assertEquals(OrderStatus.IN_STORAGE, batch.get(0).getStatus());
    }
}