package ru.nsu.vorona;

import java.util.Map;

/**
 * Утилитный класс для вычисления значений выражений.
 */
public final class Evaluator {

    /**
     * Вычисляет значение выражения при заданных значениях переменных.
     *
     * @param expression выражение для вычисления
     * @param assignments строка с присваиваниями вида "x = 10; y = 5"
     * @return числовое значение выражения
     */
    public static double eval(Expression expression, String assignments) {
        return expression.eval(AssignmentParser.parse(assignments));
    }

    /**
     * Вычисляет значение выражения при заданных значениях переменных.
     *
     * @param expression выражение для вычисления
     * @param assignments карта значений переменных
     * @return числовое значение выражения
     */
    public static double eval(Expression expression, Map<String, Double> assignments) {
        return expression.eval(assignments);
    }

    private Evaluator() {
        throw new UnsupportedOperationException("Utility class");
    }
}
