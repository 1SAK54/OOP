package ru.nsu.vorona;

import java.util.Map;

/**
 * Представляет операцию деления двух выражений.
 */
public class Div implements Expression {
    private final Expression left;
    private final Expression right;

    /**
     * Создаёт выражение деления.
     *
     * @param left левое выражение
     * @param right правое выражение
     */
    public Div(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Expression derivative(String var) {
        return new Div(
                new Sub(
                        new Mul(left.derivative(var), right),
                        new Mul(left, right.derivative(var))
                ),
                new Mul(right, right)
        );
    }

    @Override
    public double eval(Map<String, Double> assignments) {
        return left.eval(assignments) / right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue()
                    / ((Number) rightSimple).getValue();
            return new Number(result);
        }

        return new Div(leftSimple, rightSimple);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Div)) {
            return false;
        }
        Div other = (Div) obj;
        return left.equals(other.left) && right.equals(other.right);
    }

    @Override
    public int hashCode() {
        return 31 * left.hashCode() + right.hashCode();
    }

    @Override
    public String toString() {
        return "(" + left + "/" + right + ")";
    }
}
