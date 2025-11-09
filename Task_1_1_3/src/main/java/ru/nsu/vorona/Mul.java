package ru.nsu.vorona;

import java.util.Map;

/**
 * Представляет операцию умножения двух выражений.
 */
public class Mul implements Expression {
    private final Expression left;
    private final Expression right;

    /**
     * Создаёт выражение умножения.
     *
     * @param left левый множитель
     * @param right правый множитель
     */
    public Mul(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Expression derivative(String var) {
        return new Add(
                new Mul(left.derivative(var), right),
                new Mul(left, right.derivative(var))
        );
    }

    @Override
    public double eval(Map<String, Double> assignments) {
        return left.eval(assignments) * right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue()
                    * ((Number) rightSimple).getValue();
            return new Number (result);
        }

        if (leftSimple instanceof Number && ((Number) leftSimple).getValue() == 0) {
            return new Number(0);
        }

        if (rightSimple instanceof Number && ((Number) rightSimple).getValue() == 0) {
            return new Number(0);
        }

        if (leftSimple instanceof Number && ((Number) leftSimple).getValue() == 1) {
            return rightSimple;
        }

        if (rightSimple instanceof Number && ((Number) rightSimple).getValue() == 1) {
            return leftSimple;
        }

        return new Mul (leftSimple, rightSimple);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mul)) {
            return false;
        }
        Mul other = (Mul) obj;
        return (left.equals(other.left) && right.equals(other.right))
                || (left.equals(other.right) && right.equals(other.left));
    }

    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode();
    }

    @Override
    public String toString() {
        return "(" + left + "*" + right + ")";
    }
}
