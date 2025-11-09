package ru.nsu.vorona;

import java.util.Map;

/**
 * Представляет переменную в математическом выражении.
 * Поддерживает многобуквенные имена переменных.
 */
public class Variable implements Expression {
    private final String name;

    /**
     * Создаёт переменную с заданным именем.
     *
     * @param name имя переменной
     */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Expression derivative(String var) {
        if (name.equals(var)) {
            return new Number(1);
        } else {
            return new Number(0);
        }
    }

    @Override
    public double eval(Map<String, Double> assignments) {
        return assignments.get(name);
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
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
