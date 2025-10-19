package ru.nsu.vorona.Task_1_1_3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionPasrserTest {
    @Test
    void testParseSingleNumber() {
        Expression e = ExpressionParser.parse("5");
        assertEquals("5.0", e.print());
    }

    @Test
    void testParseSingleVariable() {
        Expression e = ExpressionParser.parse("x");
        assertEquals("x", e.print());
    }

    @Test
    void testParseAdd() {
        Expression e = ExpressionParser.parse("(2+3)");
        assertEquals("(2.0+3.0)", e.print());
    }

    @Test
    void testParseComplexExpression() {
        Expression e = ExpressionParser.parse("(3+(2*x))");
        assertEquals("(3.0+(2.0*x))", e.print());
    }

    @Test
    void testParseWithoutBrackets() {
        Expression e = ExpressionParser.parse("3+2*x");
        assertEquals("(3.0+(2.0*x))", e.print());
    }
}

