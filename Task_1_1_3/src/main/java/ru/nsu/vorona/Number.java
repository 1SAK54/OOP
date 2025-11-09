package ru.nsu.vorona;

import java.util.Map;

/**
 * Представляет числовую константу в математическом выражении.
 */
public class Number implements Expression {
    private final double value;

    /**
     * Создаёт числовую константу с заданным значением.
     *
     * @param value числовое значение
     */
    public Number(double value) {
        this.value = value;
    }

    /**
     * Возвращает значение числовой константы.
     *
     * @return значение константы
     */
    public double getValue() {
        return value;
    }

    @Override
    public Expression derivative(String var) {
        return new Number(0);
    }

    @Override
    public double eval(Map<String, Double> assignments) {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Number)) {
            return false;
        }
        Number other = (Number) obj;
        return Double.compare(value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
