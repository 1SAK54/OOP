package ru.nsu.vorona;

import java.util.List;

/**
 * Конфигурация пиццерии
 */
public class Config {
    private int workingTimeMs;
    private int storageCapacity;
    private int ordersCount;
    private int orderIntervalMs;
    private List<BakerConfig> bakers;
    private List<CourierConfig> couriers;

    /**
     * Возвращает время работы пиццерии
     *
     * @return время работы в миллисекундах
     */
    public int getWorkingTimeMs() {
        return workingTimeMs;
    }

    /**
     * Устанавливает время работы пиццерии
     *
     * @param workingTimeMs время работы
     */
    public void setWorkingTimeMs(int workingTimeMs) {
        this.workingTimeMs = workingTimeMs;
    }

    /**
     * Возвращает вместимость склада
     *
     * @return вместимость склада
     */
    public int getStorageCapacity() {
        return storageCapacity;
    }

    /**
     * Устанавливает вместимость склада
     *
     * @param storageCapacity вместимость склада
     */
    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    /**
     * Возвращает число заказов
     *
     * @return число заказов
     */
    public int getOrdersCount() {
        return ordersCount;
    }

    /**
     * Устанавливает число заказов
     *
     * @param ordersCount число заказов
     */
    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    /**
     * Возвращает интервал между заказами
     *
     * @return интервал в миллисекундах
     */
    public int getOrderIntervalMs() {
        return orderIntervalMs;
    }

    /**
     * Устанавливает интервал между заказами
     *
     * @param orderIntervalMs интервал между заказами
     */
    public void setOrderIntervalMs(int orderIntervalMs) {
        this.orderIntervalMs = orderIntervalMs;
    }

    /**
     * Возвращает список пекарей
     *
     * @return список пекарей
     */
    public List<BakerConfig> getBakers() {
        return bakers;
    }

    /**
     * Устанавливает список пекарей
     *
     * @param bakers список пекарей
     */
    public void setBakers(List<BakerConfig> bakers) {
        this.bakers = bakers;
    }

    /**
     * Возвращает список курьеров
     *
     * @return список курьеров
     */
    public List<CourierConfig> getCouriers() {
        return couriers;
    }

    /**
     * Устанавливает список курьеров
     *
     * @param couriers список курьеров
     */
    public void setCouriers(List<CourierConfig> couriers) {
        this.couriers = couriers;
    }

    /**
     * Конфигурация пекаря
     */
    public static class BakerConfig {
        private int id;
        private int bakeTimeMs;

        /**
         * Возвращает id пекаря
         *
         * @return id пекаря
         */
        public int getId() {
            return id;
        }

        /**
         * Устанавливает id пекаря
         *
         * @param id id пекаря
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * Возвращает время готовки
         *
         * @return время готовки
         */
        public int getBakeTimeMs() {
            return bakeTimeMs;
        }

        /**
         * Устанавливает время готовки
         *
         * @param bakeTimeMs время готовки
         */
        public void setBakeTimeMs(int bakeTimeMs) {
            this.bakeTimeMs = bakeTimeMs;
        }
    }

    /**
     * Конфигурация курьера
     */
    public static class CourierConfig {
        private int id;
        private int bagCapacity;
        private int deliveryTimeMs;

        /**
         * Возвращает id курьера
         *
         * @return id курьера
         */
        public int getId() {
            return id;
        }

        /**
         * Устанавливает id курьера
         *
         * @param id id курьера
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * Возвращает вместимость сумки
         *
         * @return вместимость сумки
         */
        public int getBagCapacity() {
            return bagCapacity;
        }

        /**
         * Устанавливает вместимость сумки
         *
         * @param bagCapacity вместимость сумки
         */
        public void setBagCapacity(int bagCapacity) {
            this.bagCapacity = bagCapacity;
        }

        /**
         * Возвращает время доставки
         *
         * @return время доставки
         */
        public int getDeliveryTimeMs() {
            return deliveryTimeMs;
        }

        /**
         * Устанавливает время доставки
         *
         * @param deliveryTimeMs время доставки
         */
        public void setDeliveryTimeMs(int deliveryTimeMs) {
            this.deliveryTimeMs = deliveryTimeMs;
        }
    }
}