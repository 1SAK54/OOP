package ru.nsu.vorona;

public final class Evaluator {
    /**
     * Вычисляет значение выражения при заданных значениях переменных.
     *
     * @param assignments строка с присваиваниями вида "x = 10; y = 5"
     * @return числовое значение выражения
     */
    public double eval(Expression expression, String assignments) {
        return expression.eval(
                AssignmentParser.parse(assignments)
        );
    }

    private Evaluator() {
        throw new UnsupportedOperationException();
    }
}
