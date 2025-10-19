package ru.nsu.vorona.Task_1_1_2;

/**
 * Перечисление, представляющее масти игральных карт.
 */
public enum Suit {
    SPADES("Пики"),
    HEARTS("Червы"),
    CLUBS("Трефы"),
    DIAMONDS("Бубны");

    /** Русское название масти. */
    private final String rusName;

    /**
     * Создаёт масть с заданным названием.
     *
     * @param rusName русское название масти
     */
    Suit(String rusName) { this.rusName = rusName; }

    /**
     * Возвращает русское название масти.
     *
     * @return русское название
     */
    public String getRusName() { return rusName; }
}
