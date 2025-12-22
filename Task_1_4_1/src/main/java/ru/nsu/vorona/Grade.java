package ru.nsu.vorona;

/**
 * Итоговая оценка по 5‑балльной шкале.
 */
public enum Grade {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int numeric;

    Grade(int numeric) {
        this.numeric = numeric;
    }

    public int getNumeric() {
        return numeric;
    }

    public boolean isExcellent() {
        return this == FIVE;
    }

    public boolean isSatisfactory() {
        return this == THREE;
    }

    public boolean isBad() {
        return this == TWO;
    }
}
