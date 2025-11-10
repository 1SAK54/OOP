package ru.nsu.vorona;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилитный класс для парсинга строк с присваиваниями переменных.
 * Преобразует строку вида "x = 10; y = 5" в карту значений переменных.
 */
public final class AssignmentParser {

    /**
     * Парсит строку с присваиваниями переменных.
     *
     * @param assignments строка вида "x = 10; y = 5"
     * @return карта с именами переменных и их значениями
     */
    public static Map<String, Double> parse(String assignments) {
        Map<String, Double> map = new HashMap<>();
        String[] parts = assignments.split(";");

        for (String assignment : parts) {
            String[] pair = assignment.split("=");
            String name = pair[0].trim();
            String valueStr = pair[1].trim();
            double value = Double.parseDouble(valueStr);
            map.put(name, value);
        }

        return map;
    }

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private AssignmentParser() {
        throw new UnsupportedOperationException("Utility class");
    }
}
