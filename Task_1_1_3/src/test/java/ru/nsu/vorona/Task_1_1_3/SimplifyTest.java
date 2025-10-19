package ru.nsu.vorona.Task_1_1_3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimplifyTest {
    @Test
    void testSimplifyConstantAdd() {
        Expression e = new Add(new Number(2), new Number(3));
        Expression simplified = e.simplify();
        assertEquals("5.0", simplified.print());
    }

    @Test
    void testSimplifyConstantSub() {
        Expression e = new Sub(new Number(10), new Number(3));
        Expression simplified = e.simplify();
        assertEquals("7.0", simplified.print());
    }

    @Test
    void testSimplifyConstantMul() {
        Expression e = new Mul(new Number(4), new Number(5));
        Expression simplified = e.simplify();
        assertEquals("20.0", simplified.print());
    }

    @Test
    void testSimplifyConstantDiv() {
        Expression e = new Div(new Number(10), new Number(2));
        Expression simplified = e.simplify();
        assertEquals("5.0", simplified.print());
    }

    @Test
    void testSimplifyEqualExpressionSub() {
        Expression a = new Add(new Number(10), new Variable("x"));
        Expression b = new Add(new Variable("x"), new Number(10));
        Expression c = new Sub(a, b);
        Expression simplified = c.simplify();
        assertEquals("0.0", simplified.print());
    }
}
