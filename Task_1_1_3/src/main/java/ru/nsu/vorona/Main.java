package ru.nsu.vorona;

/**
 * Главный класс программы для демонстрации работы с математическими выражениями, как в примере.
 */
public class Main {

    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        System.out.println(e.print());
        Expression de = e.derivative("x");
        System.out.println(de.print());
        double result = e.eval("x = 10; y = 13");
        System.out.println(result);
    }
}
