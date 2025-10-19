package ru.nsu.vorona;

/**
 * Представляет операцию деления двух выражений.
 */
public class Div extends Expression {
    private Expression left;
    private Expression right;

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
    public String print() {
        return "(" + left.print() + "/" + right.print() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Div (new Sub(
                    new Mul(left.derivative(var), right),
                    new Mul(left, right.derivative(var))
                ),
                new Mul(right, right)
        );
    }

    @Override
    public double eval(String assignments) {
        return left.eval(assignments) / right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue() / ((Number) rightSimple).getValue();
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
}
