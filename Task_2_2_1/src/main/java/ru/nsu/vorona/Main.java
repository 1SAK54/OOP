package ru.nsu.vorona;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Точка входа
 */
public class Main {

    /**
     * Запускает программу
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        String configName = args.length > 0 ? args[0] : "config.json";
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Config config = readConfig(objectMapper, configName);

            if (config == null) {
                System.err.println("Failed to read config: " + configName);
                return;
            }

            Pizzeria pizzeria = new Pizzeria(config);
            pizzeria.run();
        } catch (IOException e) {
            System.err.println("Failed to read config: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Execution interrupted");
        }
    }

    /**
     * Читает конфиг из файла или resources
     *
     * @param objectMapper объект для чтения JSON
     * @param configName имя файла или путь
     * @return конфигурация или null
     * @throws IOException если произошла ошибка чтения
     */
    private static Config readConfig(ObjectMapper objectMapper, String configName) throws IOException {
        File file = new File(configName);
        if (file.exists()) {
            return objectMapper.readValue(file, Config.class);
        }

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(configName)) {
            if (inputStream == null) {
                return null;
            }
            return objectMapper.readValue(inputStream, Config.class);
        }
    }
}