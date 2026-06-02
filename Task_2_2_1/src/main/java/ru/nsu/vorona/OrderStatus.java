package ru.nsu.vorona;

/**
 * Состояния заказа
 */
public enum OrderStatus {
    IN_QUEUE,
    BAKING,
    IN_STORAGE,
    DELIVERING,
    DELIVERED
}