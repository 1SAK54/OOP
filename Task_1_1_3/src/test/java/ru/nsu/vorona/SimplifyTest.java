package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для проверки упрощения математических выражений.
 * Проверяет правила упрощения: вычисление констант, умножение на 0 и 1,
 * вычитание одинаковых выражений.
 */
public class SimplifyTest {

    /**
     * Проверяет упрощение суммы двух констант.
     */
    @Test
    void testSimplifyConstantAdd() {
        Expression e = new Add(new Number(2), new Number(3));
        Expression simplified = e.simplify();
        assertEquals("5.0", simplified.toString());
    }

    /**
     * Проверяет упрощение разности двух констант.
     */
    @Test
    void testSimplifyConstantSub() {
        Expression e = new Sub(new Number(10), new Number(3));
        Expression simplified = e.simplify();
        assertEquals("7.0", simplified.toString());
    }

    /**
     * Проверяет упрощение произведения двух констант.
     */
    @Test
    void testSimplifyConstantMul() {
        Expression e = new Mul(new Number(4), new Number(5));
        Expression simplified = e.simplify();
        assertEquals("20.0", simplified.toString());
    }

    /**
     * Проверяет упрощение деления двух констант.
     */
    @Test
    void testSimplifyConstantDiv() {
        Expression e = new Div(new Number(10), new Number(2));
        Expression simplified = e.simplify();
        assertEquals("5.0", simplified.toString());
    }

    /**
     * Проверяет упрощение вычитания двух одинаковых выражений с учётом коммутативности.
     */
    @Test
    void testSimplifyEqualExpressionSub() {
        Expression a = new Add(new Number(10), new Variable("x"));
        Expression b = new Add(new Variable("x"), new Number(10));
        Expression c = new Sub(a, b);
        Expression simplified = c.simplify();
        assertEquals("0.0", simplified.toString());
    }

    /**
     * Проверяет упрощение умножения на ноль слева.
     */
    @Test
    void testSimplifyMulByZeroLeft() {
        Expression e = new Mul(new Number(0), new Variable("x"));
        Expression simplified = e.simplify();
        assertEquals("0.0", simplified.toString());
    }

    /**
     * Проверяет упрощение умножения на ноль справа.
     */
    @Test
    void testSimplifyMulByZeroRight() {
        Expression e = new Mul(new Variable("x"), new Number(0));
        Expression simplified = e.simplify();
        assertEquals("0.0", simplified.toString());
    }

    /**
     * Проверяет упрощение умножения на единицу слева.
     */
    @Test
    void testSimplifyMulByOneLeft() {
        Expression e = new Mul(new Number(1), new Variable("x"));
        Expression simplified = e.simplify();
        assertEquals("x", simplified.toString());
    }

    /**
     * Проверяет упрощение умножения на единицу справа.
     */
    @Test
    void testSimplifyMulByOneRight() {
        Expression e = new Mul(new Variable("x"), new Number(1));
        Expression simplified = e.simplify();
        assertEquals("x", simplified.toString());
    }

    /**
     * Проверяет упрощение вычитания одинаковых переменных.
     */
    @Test
    void testSimplifySubSameVariable() {
        Variable x = new Variable("x");
        Expression e = new Sub(x, x);
        Expression simplified = e.simplify();
        assertEquals("0.0", simplified.toString());
    }

    /**
     * Проверяет, что упрощение не изменяет исходное выражение.
     */
    @Test
    void testSimplifyDoesNotModifyOriginal() {
        Expression e = new Mul(new Variable("x"), new Number(0));
        String original = e.toString();

        Expression simplified = e.simplify();

        assertEquals(original, e.toString());
        assertEquals("0.0", simplified.toString());
    }

    /**
     * Проверяет упрощение сложного выражения с вложенными операциями.
     */
    @Test
    void testSimplifyNestedExpression() {
        Expression e = new Mul(new Add(new Number(2), new Number(3)), new Variable("x"));
        Expression simplified = e.simplify();
        assertEquals("(5.0*x)", simplified.toString());
    }

    /**
     * Проверяет упрощение производной простого выражения.
     */
    @Test
    void testSimplifyDerivativeResult() {
        Expression e = new Mul(new Number(2), new Variable("x"));
        Expression derivative = e.derivative("x");
        Expression simplified = derivative.simplify();
        assertEquals("2.0", simplified.toString());
    }

    /**
     * Проверяет упрощение производной сложного выражения.
     */
    @Test
    void testSimplifyComplexDerivative() {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        Expression derivative = e.derivative("x");
        Expression simplified = derivative.simplify();
        assertEquals("2.0", simplified.toString());
    }

    /**
     * Проверяет упрощение выражения с умножением на ноль в сложной структуре.
     */
    @Test
    void testSimplifyComplexMulByZero() {
        Expression e = new Add(
                new Mul(new Variable("x"), new Number(0)),
                new Mul(new Number(5), new Number(1))
        );
        Expression simplified = e.simplify();
        assertEquals("5.0", simplified.toString());
    }

    /**
     * Проверяет, что переменная остаётся неизменной при упрощении.
     */
    @Test
    void testSimplifyVariable() {
        Expression e = new Variable("x");
        Expression simplified = e.simplify();
        assertEquals("x", simplified.toString());
    }

    /**
     * Проверяет, что число остаётся неизменным при упрощении.
     */
    @Test
    void testSimplifyNumber() {
        Expression e = new Number(42);
        Expression simplified = e.simplify();
        assertEquals("42.0", simplified.toString());
    }
}
