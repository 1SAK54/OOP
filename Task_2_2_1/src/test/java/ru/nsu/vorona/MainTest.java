package ru.nsu.vorona;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Тесты точки входа
 */
class MainTest {

    /**
     * Проверяет запуск программы с корректным конфигом
     *
     * @param tempDir временная директория
     * @throws IOException если произошла ошибка записи файла
     */
    @Test
    void shouldRunMainWithValidConfig(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("config.json");

        String json = """
                {
                  "workingTimeMs": 1000,
                  "storageCapacity": 3,
                  "ordersCount": 5,
                  "orderIntervalMs": 50,
                  "bakers": [
                    { "id": 1, "bakeTimeMs": 50 }
                  ],
                  "couriers": [
                    { "id": 1, "bagCapacity": 2, "deliveryTimeMs": 50 }
                  ]
                }
                """;

        Files.writeString(configFile, json);

        assertDoesNotThrow(() -> Main.main(new String[]{configFile.toString()}));
    }

    /**
     * Проверяет запуск программы без аргументов
     */
    @Test
    void shouldHandleEmptyArgs() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }

    /**
     * Проверяет запуск программы с несуществующим файлом
     */
    @Test
    void shouldHandleInvalidFilePath() {
        assertDoesNotThrow(() -> Main.main(new String[]{"missing_config.json"}));
    }
}