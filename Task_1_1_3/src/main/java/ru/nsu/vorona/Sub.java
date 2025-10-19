package ru.nsu.vorona.Task_1_1_3;

/**
 * Представляет операцию вычитания двух выражений.
 */
public class Sub extends Expression{
    private Expression left;
    private Expression right;

    /**
     * Создаёт выражение вычитания.
     *
     * @param left левое выражение
     * @param right правое выражение
     */
    public Sub(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "-" + right.print() + ")";
    }

    @Override
    public Expression derivative(String var) {
        return new Sub(left.derivative(var), right.derivative((var)));
    }

    @Override
    public double eval(String assignments) {
        return left.eval(assignments) - right.eval(assignments);
    }

    @Override
    public Expression simplify() {
        Expression leftSimple = left.simplify();
        Expression rightSimple = right.simplify();

        if (leftSimple instanceof Number && rightSimple instanceof Number) {
            double result = ((Number) leftSimple).getValue() - ((Number) rightSimple).getValue();
            return new Number(result);
        }

        if (leftSimple.equals(rightSimple)) {
            return new Number(0);
        }

        return new Sub(leftSimple, rightSimple);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sub)) return false;
        Sub other = (Sub) obj;
        return left.equals(other.left) && right.equals(other.right);
    }
}
