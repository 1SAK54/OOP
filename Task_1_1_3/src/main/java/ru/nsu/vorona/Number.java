package ru.nsu.vorona;

/**
 * Представляет числовую константу в математическом выражении.
 */
public class Number extends Expression {
    private double value;

    /**
     * Создаёт числовую константу.
     *
     * @param value числовое значение константы
     */
    public Number(double value) {
        this.value = value;
    }

    /**
     * Возвращает значение числа.
     *
     * @return значение константы
     */
    public double getValue() {
        return value;
    }

    @Override
    public String print() {
        return String.valueOf(value);
    }

    @Override
    public Expression derivative(String var) {
        return new Number(0);
    }

    @Override
    public double eval(String assignment) {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Number)) return false;
        Number other = (Number) obj;
        return Double.compare(value, other.value) == 0;
    }
}
