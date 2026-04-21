package ru.nsu.vorona;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Очередь заказов
 */
public class OrderQueue {
    private final Queue<Order> orders = new LinkedList<>();
    private boolean acceptingOrders = true;

    /**
     * Добавляет заказ в очередь
     *
     * @param order заказ
     * @return true, если заказ добавлен
     */
    public synchronized boolean add(Order order) {
        if (!acceptingOrders) {
            return false;
        }
        orders.add(order);
        notifyAll();
        return true;
    }

    /**
     * Берёт заказ из очереди
     *
     * @return заказ или null, если работа завершена
     * @throws InterruptedException если поток прерван
     */
    public synchronized Order take() throws InterruptedException {
        while (orders.isEmpty() && acceptingOrders) {
            wait();
        }

        if (orders.isEmpty()) {
            return null;
        }

        Order order = orders.poll();
        notifyAll();
        return order;
    }

    /**
     * Закрывает приём новых заказов
     */
    public synchronized void close() {
        acceptingOrders = false;
        notifyAll();
    }

    /**
     * Проверяет, закрыта ли очередь и пуста ли она
     *
     * @return true, если новых заказов не будет и очередь пуста
     */
    public synchronized boolean isClosedAndEmpty() {
        return !acceptingOrders && orders.isEmpty();
    }
}