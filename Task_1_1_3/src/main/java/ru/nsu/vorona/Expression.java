package ru.nsu.vorona;

import java.util.Map;

/**
 * Интерфейс, представляющий математическое выражение.
 * Поддерживает операции печати (toString), дифференцирования, вычисления и упрощения.
 * выбрасывает исключение {@link ArithmeticException} в случае ошибки
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
     * Упрощает выражение по заданным правилам.
     *
     * @return новое упрощённое выражение
     */
    Expression simplify();
}

