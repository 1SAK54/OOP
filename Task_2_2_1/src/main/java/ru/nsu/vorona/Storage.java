package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Склад готовых заказов
 */
public class Storage {
    private final Queue<Order> readyOrders = new LinkedList<>();
    private final int capacity;
    private boolean productionFinished = false;

    /**
     * Создаёт склад
     *
     * @param capacity вместимость склада
     */
    public Storage(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Кладёт заказ на склад
     *
     * @param order готовый заказ
     * @throws InterruptedException если поток прерван
     */
    public synchronized void put(Order order) throws InterruptedException {
        while (readyOrders.size() >= capacity) {
            wait();
        }

        readyOrders.add(order);
        notifyAll();
    }

    /**
     * Забирает партию заказов со склада
     *
     * @param maxCount максимум заказов
     * @return список заказов
     * @throws InterruptedException если поток прерван
     */
    public synchronized List<Order> takeBatch(int maxCount) throws InterruptedException {
        while (readyOrders.isEmpty() && !productionFinished) {
            wait();
        }

        if (readyOrders.isEmpty() && productionFinished) {
            return List.of();
        }

        int count = Math.min(maxCount, readyOrders.size());
        List<Order> batch = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            batch.add(readyOrders.poll());
        }

        notifyAll();
        return batch;
    }

    /**
     * Помечает завершение производства
     */
    public synchronized void finishProduction() {
        productionFinished = true;
        notifyAll();
    }
}