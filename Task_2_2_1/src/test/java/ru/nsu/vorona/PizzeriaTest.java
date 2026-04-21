package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Тесты пиццерии
 */
class PizzeriaTest {

    /**
     * Проверяет запуск пиццерии с простой конфигурацией
     */
    @Test
    void shouldRunPizzeriaWithoutExceptions() {
        Config config = new Config();
        config.setWorkingTimeMs(1000);
        config.setStorageCapacity(3);
        config.setOrdersCount(5);
        config.setOrderIntervalMs(50);

        Config.BakerConfig baker = new Config.BakerConfig();
        baker.setId(1);
        baker.setBakeTimeMs(50);

        Config.CourierConfig courier = new Config.CourierConfig();
        courier.setId(1);
        courier.setBagCapacity(2);
        courier.setDeliveryTimeMs(50);

        config.setBakers(List.of(baker));
        config.setCouriers(List.of(courier));

        Pizzeria pizzeria = new Pizzeria(config);

        assertDoesNotThrow(pizzeria::run);
    }
}