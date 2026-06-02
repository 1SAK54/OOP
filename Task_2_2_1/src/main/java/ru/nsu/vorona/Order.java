package ru.nsu.vorona;

/**
 * Заказ пиццы
 */
public class Order {
    private final int id;
    private OrderStatus status;

    /**
     * Создаёт заказ
     *
     * @param id номер заказа
     */
    public Order(int id) {
        this.id = id;
    }

    /**
     * Возвращает номер заказа
     *
     * @return номер заказа
     */
    public int getId() {
        return id;
    }

    /**
     * Возвращает состояние заказа
     *
     * @return состояние заказа
     */
    public synchronized OrderStatus getStatus() {
        return status;
    }

    /**
     * Устанавливает состояние заказа
     *
     * @param status новое состояние
     */
    public synchronized void setStatus(OrderStatus status) {
        this.status = status;
    }
}