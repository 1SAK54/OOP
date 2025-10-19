package ru.nsu.vorona;

import java.util.Map;

/**
 * Представляет переменную в математическом выражении.
 */
public class Variable extends Expression {
    private String name;

    /**
     * Создаёт переменную с заданным именем.
     *
     * @param name имя переменной
     */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String print() {
        return name;
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
    public double eval(String assignments) {
        Map<String, Double> values = AssignmentParser.parse(assignments);
        return values.get(name);
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
}
