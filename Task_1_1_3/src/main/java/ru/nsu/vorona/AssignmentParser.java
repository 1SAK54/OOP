package ru.nsu.vorona;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилитный класс для парсинга строк с присваиваниями переменных.
 */
public class AssignmentParser {
    /**
     * Парсит строку с присваиваниями переменных и их значений.gradle jacocoTestCoverageVerification
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
            Double value = Double.parseDouble(pair[1].trim());
            map.put(name, value);
        }

        return map;
    }
}

