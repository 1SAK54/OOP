package ru.nsu.vorona;

import java.util.List;

/**
 * Поток курьера
 */
public class Courier extends Thread {
    private final int courierId;
    private final int bagCapacity;
    private final int deliveryTimeMs;
    private final Storage storage;

    /**
     * Создаёт курьера
     *
     * @param courierId идентификатор курьера
     * @param bagCapacity вместимость сумки
     * @param deliveryTimeMs время доставки
     * @param storage склад
     */
    public Courier(int courierId, int bagCapacity, int deliveryTimeMs, Storage storage) {
        this.courierId = courierId;
        this.bagCapacity = bagCapacity;
        this.deliveryTimeMs = deliveryTimeMs;
        this.storage = storage;
        setName("Courier-" + courierId);
    }

    /**
     * Запускает работу курьера
     */
    @Override
    public void run() {
        try {
            while (true) {
                List<Order> batch = storage.takeBatch(bagCapacity);
                if (batch.isEmpty()) {
                    return;
                }

                for (Order order : batch) {
                    order.setStatus(OrderStatus.DELIVERING);
                    print(order);
                }

                Thread.sleep(deliveryTimeMs);

                for (Order order : batch) {
                    order.setStatus(OrderStatus.DELIVERED);
                    print(order);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Печатает состояние заказа
     *
     * @param order заказ
     */
    private void print(Order order) {
        System.out.println("[" + order.getId() + "] " + order.getStatus());
    }
}