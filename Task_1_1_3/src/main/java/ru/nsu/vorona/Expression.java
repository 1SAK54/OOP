package ru.nsu.vorona;

import java.util.Map;

/**
 * Интерфейс, представляющий математическое выражение.
 * Поддерживает операции печати (toString), дифференцирования, вычисления и упрощения.
 */
public interface Expression {

    /**
     * Вычисляет производную выражения по заданной переменной.
     *
     * @param var имя переменной для дифференцирования
     * @return новое выражение, представляющее производную
     */
    Expression derivative(String var);

    /**
     * Вычисляет значение выражения при заданных значениях переменных.
     *
     * @param assignments значения переменных
     * @return числовое значение выражения
     */
    double eval(Map<String, Double> assignments);

    /**
     * Вычисляет значение выражения при заданных значениях переменных из строки.
     * Это вспомогательный метод для удобства использования.
     *
     * @param assignments строка с присваиваниями вида "x = 10; y = 5"
     * @return числовое значение выражения
     */
    default double eval(String assignments) {
        return eval(AssignmentParser.parse(assignments));
    }

    /**
     * Упрощает выражение по заданным правилам.
     *
     * @return новое упрощённое выражение
     */
    Expression simplify();
}
