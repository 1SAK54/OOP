package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для проверки парсера математических выражений.
 * Проверяет корректность преобразования строк в объекты Expression.
 */
public class ExpressionParserTest {

    /**
     * Проверяет парсинг одиночного числа.
     */
    @Test
    void testParseSingleNumber() {
        Expression e = ExpressionParser.parse("5");
        assertEquals("5.0", e.toString());
    }

    /**
     * Проверяет парсинг одиночной переменной.
     */
    @Test
    void testParseSingleVariable() {
        Expression e = ExpressionParser.parse("x");
        assertEquals("x", e.toString());
    }

    /**
     * Проверяет парсинг простого сложения в скобках.
     */
    @Test
    void testParseAdd() {
        Expression e = ExpressionParser.parse("(2+3)");
        assertEquals("(2.0+3.0)", e.toString());
    }

    /**
     * Проверяет парсинг сложного вложенного выражения.
     */
    @Test
    void testParseComplexExpression() {
        Expression e = ExpressionParser.parse("(3+(2*x))");
        assertEquals("(3.0+(2.0*x))", e.toString());
    }

    /**
     * Проверяет парсинг выражения без внешних скобок с учётом приоритета операций.
     */
    @Test
    void testParseWithoutBrackets() {
        Expression e = ExpressionParser.parse("3+2*x");
        assertEquals("(3.0+(2.0*x))", e.toString());
    }

    /**
     * Проверяет парсинг вычитания.
     */
    @Test
    void testParseSub() {
        Expression e = ExpressionParser.parse("(10-3)");
        assertEquals("(10.0-3.0)", e.toString());
    }

    /**
     * Проверяет парсинг умножения.
     */
    @Test
    void testParseMul() {
        Expression e = ExpressionParser.parse("(4*5)");
        assertEquals("(4.0*5.0)", e.toString());
    }

    /**
     * Проверяет парсинг деления.
     */
    @Test
    void testParseDiv() {
        Expression e = ExpressionParser.parse("(10/2)");
        assertEquals("(10.0/2.0)", e.toString());
    }

    /**
     * Проверяет парсинг выражения с многобуквенной переменной.
     */
    @Test
    void testParseMultiLetterVariable() {
        Expression e = ExpressionParser.parse("abc");
        assertEquals("abc", e.toString());
    }

    /**
     * Проверяет парсинг дробного числа.
     */
    @Test
    void testParseDecimalNumber() {
        Expression e = ExpressionParser.parse("3.14");
        assertEquals("3.14", e.toString());
    }

    /**
     * Проверяет парсинг выражения с вложенными скобками.
     */
    @Test
    void testParseNestedBrackets() {
        Expression e = ExpressionParser.parse("((2+3)*x)");
        assertEquals("((2.0+3.0)*x)", e.toString());
    }

    /**
     * Проверяет парсинг выражения без скобок с несколькими операциями.
     */
    @Test
    void testParseMultipleOperationsWithoutBrackets() {
        Expression e = ExpressionParser.parse("2*x+3*y");
        assertEquals("((2.0*x)+(3.0*y))", e.toString());
    }

    /**
     * Проверяет корректность вычисления распарсенного выражения.
     */
    @Test
    void testParseAndEval() {
        Expression e = ExpressionParser.parse("(3+(2*x))");
        assertEquals(23.0, e.eval("x = 10"));
    }

    /**
     * Проверяет корректность дифференцирования распарсенного выражения.
     */
    @Test
    void testParseAndDerivative() {
        Expression e = ExpressionParser.parse("(2*x)");
        Expression de = e.derivative("x");
        assertEquals("((0.0*x)+(2.0*1.0))", de.toString());
    }
}