package ru.nsu.vorona;

/**
 * Поток пекаря
 */
public class Baker extends Thread {
    private final int bakerId;
    private final int bakeTimeMs;
    private final OrderQueue orderQueue;
    private final Storage storage;

    /**
     * Создаёт пекаря
     *
     * @param bakerId идентификатор пекаря
     * @param bakeTimeMs время готовки
     * @param orderQueue очередь заказов
     * @param storage склад
     */
    public Baker(int bakerId, int bakeTimeMs, OrderQueue orderQueue, Storage storage) {
        this.bakerId = bakerId;
        this.bakeTimeMs = bakeTimeMs;
        this.orderQueue = orderQueue;
        this.storage = storage;
        setName("Baker-" + bakerId);
    }

    /**
     * Запускает работу пекаря
     */
    @Override
    public void run() {
        try {
            while (true) {
                Order order = orderQueue.take();
                if (order == null) {
                    return;
                }

                order.setStatus(OrderStatus.BAKING);
                print(order);

                Thread.sleep(bakeTimeMs);

                storage.put(order);

                order.setStatus(OrderStatus.IN_STORAGE);
                print(order);
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