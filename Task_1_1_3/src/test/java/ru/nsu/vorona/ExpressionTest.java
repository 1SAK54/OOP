package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Тесты для проверки основных операций с математическими выражениями.
 * Проверяет методы toString(), eval() и derivative() для всех типов выражений.
 */
public class ExpressionTest {

    /**
     * Проверяет печать числовой константы.
     */
    @Test
    void testNumbertoString() {
        Expression e = new Number(7);
        assertEquals("7.0", e.toString());
    }

    /**
     * Проверяет вычисление значения переменной с присваиванием.
     */
    @Test
    void testVariabletoString() {
        Expression e = new Variable("x");
        assertEquals(10.0, e.eval("x = 10"));
    }

    /**
     * Проверяет вычисление суммы числа и переменной.
     */
    @Test
    void testAddEval() {
        Expression e = new Add(new Number(3), new Variable("x"));
        double result = e.eval("x = 10");
        assertEquals(13.0, result);
    }

    /**
     * Проверяет вычисление производной произведения константы на переменную.
     */
    @Test
    void testMulDerivative() {
        Expression e = new Mul(new Number(2), new Variable("x"));
        Expression de = e.derivative("x");
        assertEquals("((0.0*x)+(2.0*1.0))", de.toString());
    }

    /**
     * Проверяет производную константы (должна быть 0).
     */
    @Test
    void testNumberDerivative() {
        Expression e = new Number(5);
        Expression de = e.derivative("x");
        assertEquals("0.0", de.toString());
    }

    /**
     * Проверяет производную переменной по самой себе (должна быть 1).
     */
    @Test
    void testVariableDerivativeSame() {
        Expression e = new Variable("x");
        Expression de = e.derivative("x");
        assertEquals("1.0", de.toString());
    }

    /**
     * Проверяет производную переменной по другой переменной (должна быть 0).
     */
    @Test
    void testVariableDerivativeDifferent() {
        Expression e = new Variable("y");
        Expression de = e.derivative("x");
        assertEquals("0.0", de.toString());
    }

    /**
     * Проверяет печать выражения сложения.
     */
    @Test
    void testAddtoString() {
        Expression e = new Add(new Number(2), new Number(3));
        assertEquals("(2.0+3.0)", e.toString());
    }

    /**
     * Проверяет печать выражения вычитания.
     */
    @Test
    void testSubtoString() {
        Expression e = new Sub(new Number(10), new Number(3));
        assertEquals("(10.0-3.0)", e.toString());
    }

    /**
     * Проверяет печать выражения умножения.
     */
    @Test
    void testMultoString() {
        Expression e = new Mul(new Number(4), new Number(5));
        assertEquals("(4.0*5.0)", e.toString());
    }

    /**
     * Проверяет печать выражения деления.
     */
    @Test
    void testDivtoString() {
        Expression e = new Div(new Number(10), new Number(2));
        assertEquals("(10.0/2.0)", e.toString());
    }

    /**
     * Проверяет вычисление выражения вычитания.
     */
    @Test
    void testSubEval() {
        Expression e = new Sub(new Number(20), new Variable("x"));
        assertEquals(10.0, e.eval("x = 10"));
    }

    /**
     * Проверяет вычисление выражения умножения.
     */
    @Test
    void testMulEval() {
        Expression e = new Mul(new Number(3), new Variable("x"));
        assertEquals(30.0, e.eval("x = 10"));
    }

    /**
     * Проверяет вычисление выражения деления.
     */
    @Test
    void testDivEval() {
        Expression e = new Div(new Number(100), new Variable("x"));
        assertEquals(10.0, e.eval("x = 10"));
    }

    /**
     * Проверяет производную суммы по правилу (f+g)' = f' + g'.
     */
    @Test
    void testAddDerivative() {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        Expression de = e.derivative("x");
        assertEquals("(0.0+((0.0*x)+(2.0*1.0)))", de.toString());
    }

    /**
     * Проверяет производную разности.
     */
    @Test
    void testSubDerivative() {
        Expression e = new Sub(new Variable("x"), new Number(5));
        Expression de = e.derivative("x");
        assertEquals("(1.0-0.0)", de.toString());
    }

    /**
     * Проверяет производную частного по правилу (f/g)' = (f'*g - f*g') / g^2.
     */
    @Test
    void testDivDerivative() {
        Expression e = new Div(new Variable("x"), new Number(2));
        Expression de = e.derivative("x");
        assertEquals("(((1.0*2.0)-(x*0.0))/(2.0*2.0))", de.toString());
    }

    /**
     * Проверяет вычисление выражения с несколькими переменными.
     */
    @Test
    void testMultipleVariablesEval() {
        Expression e = new Add(new Variable("x"), new Variable("y"));
        assertEquals(15.0, e.eval("x = 5; y = 10"));
    }

    /**
     * Проверяет, что дифференцирование не изменяет исходное выражение.
     */
    @Test
    void testDerivativeDoesNotModifyOriginal() {
        Expression e = new Mul(new Number(2), new Variable("x"));
        String original = e.toString();

        Expression de = e.derivative("x");

        assertEquals(original, e.toString());
        assertNotEquals(original, de.toString());
    }

    /**
     * Проверяет вычисление сложного вложенного выражения.
     */
    @Test
    void testComplexExpressionEval() {
        Expression e = new Add(
                new Mul(new Number(2), new Variable("x")),
                new Mul(new Number(3), new Variable("y"))
        );
        assertEquals(35.0, e.eval("x = 10; y = 5"));
    }
}
