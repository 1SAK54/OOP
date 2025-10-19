package ru.nsu.vorona;

/**
 * Представляет операцию сложения двух выражений.
 */
public class Add extends Expression {
    private Expression left;
    private Expression right;

    /**
     * Создаёт выражение сложения.
     *
     * @param left левое выражение
     * @param right правое выражение
     */
    public Add(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "+" + right.print() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Add(left.derivative(var), right.derivative((var)));
    }

    @Override
    public double eval(String assignments) {
        return left.eval(assignments) + right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue() + ((Number) rightSimple).getValue();
            return new Number(result);
        }

        return new Add(leftSimple, rightSimple);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Add)) {
            return false;
        }
        Add other = (Add) obj;

        return (left.equals(other.left) && right.equals(other.right))
                || (left.equals(other.right) && right.equals(other.left));
    }
}
