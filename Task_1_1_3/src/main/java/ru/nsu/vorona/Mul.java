package ru.nsu.vorona;

/**
 * Представляет операцию умножения двух выражений.
 */
public class Mul extends Expression{
    private Expression left;
    private Expression right;

    /**
     * Создаёт выражение умножения.
     *
     * @param left левое выражение
     * @param right правое выражение
     */
    public Mul(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "*" + right.print() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Add(
                new Mul(left.derivative(var), right),
                new Mul(left, right.derivative(var))
        );
    }

    @Override
    public double eval(String assignments) {
        return left.eval(assignments) * right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue() * ((Number) rightSimple).getValue();
            return new Number(result);
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

        return new Mul(leftSimple, rightSimple);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mul)) return false;
        Mul other = (Mul) obj;

        return (left.equals(other.left) && right.equals(other.right)) ||
                (left.equals(other.right) && right.equals(other.left));
    }
}
