package ru.nsu.vorona;

/**
 * Главный класс программы для демонстрации работы с математическими выражениями.
 * Содержит примеры из условия задачи.
 */
public class Main {

    /**
     * Точка входа в программу.
     * Демонстрирует создание выражения, вычисление производной и значения.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        System.out.println(e.toString());

        Expression de = e.derivative("x");
        System.out.println(de.toString());

        // Используем Evaluator.eval вместо e.eval
        double result = Evaluator.eval(e, "x = 10");
        System.out.println(result);
    }
}
