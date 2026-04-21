package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.List;

/**
 * Пиццерия
 */
public class Pizzeria {
    private final Config config;
    private final OrderQueue orderQueue;
    private final Storage storage;
    private final List<Baker> bakers = new ArrayList<>();
    private final List<Courier> couriers = new ArrayList<>();

    /**
     * Создаёт пиццерию
     *
     * @param config конфигурация
     */
    public Pizzeria(Config config) {
        this.config = config;
        this.orderQueue = new OrderQueue();
        this.storage = new Storage(config.getStorageCapacity());
    }

    /**
     * Запускает работу пиццерии
     *
     * @throws InterruptedException если поток прерван
     */
    public void run() throws InterruptedException {
        createWorkers();
        startWorkers();

        Thread orderProducer = createOrderProducer();
        orderProducer.start();

        orderProducer.join();

        for (Baker baker : bakers) {
            baker.join();
        }

        storage.finishProduction();

        for (Courier courier : couriers) {
            courier.join();
        }
    }

    /**
     * Создаёт работников
     */
    private void createWorkers() {
        for (Config.BakerConfig bakerConfig : config.getBakers()) {
            bakers.add(new Baker(
                    bakerConfig.getId(),
                    bakerConfig.getBakeTimeMs(),
                    orderQueue,
                    storage
            ));
        }

        for (Config.CourierConfig courierConfig : config.getCouriers()) {
            couriers.add(new Courier(
                    courierConfig.getId(),
                    courierConfig.getBagCapacity(),
                    courierConfig.getDeliveryTimeMs(),
                    storage
            ));
        }
    }

    /**
     * Запускает работников
     */
    private void startWorkers() {
        for (Baker baker : bakers) {
            baker.start();
        }

        for (Courier courier : couriers) {
            courier.start();
        }
    }

    /**
     * Создаёт поток генерации заказов
     *
     * @return поток генерации заказов
     */
    private Thread createOrderProducer() {
        return new Thread(() -> {
            long deadline = System.currentTimeMillis() + config.getWorkingTimeMs();

            for (int i = 1; i <= config.getOrdersCount(); i++) {
                if (System.currentTimeMillis() > deadline) {
                    break;
                }

                Order order = new Order(i);
                order.setStatus(OrderStatus.IN_QUEUE);

                boolean added = orderQueue.add(order);
                if (!added) {
                    break;
                }

                System.out.println("[" + order.getId() + "] " + order.getStatus());

                try {
                    Thread.sleep(config.getOrderIntervalMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            orderQueue.close();
        }, "OrderProducer");
    }
}