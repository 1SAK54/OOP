package ru.nsu.vorona.Task_1_1_3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {
    @Test
    void testNumberPrint() {
        Expression e = new Number(7);
        assertEquals("7.0", e.print());
    }

    @Test
    void testVariablePrint() {
        Expression e = new Variable("x");
        assertEquals(10.0, e.eval("x = 10"));
    }

    @Test
    void testAddEval() {
        Expression e = new Add(new Number(3), new Variable("x"));
        double result = e.eval("x = 10");
        assertEquals(13.0, result);
    }

    @Test
    void testMulDerivative() {
        Expression e = new Mul(new Number(2), new Variable("x"));
        Expression de = e.derivative("x");
        assertEquals("((0.0*x)+(2.0*1.0))", de.print());
    }
}
