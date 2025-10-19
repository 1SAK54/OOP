package ru.nsu.vorona.Task_1_1_2;

/**
 * Перечисление, представляющее ранги карт и их значения.
 */
public enum Rank {
    TWO("Двойка", 2),
    THREE("Тройка", 3),
    FOUR("Четвёрка", 4),
    FIVE("Пятёрка", 5),
    SIX("Шестёрка", 6),
    SEVEN("Семёрка", 7),
    EIGHT("Восьмёрка", 8),
    NINE("Девятка", 9),
    TEN("Десятка", 10),
    JACK("Валет", 10),
    QUEEN("Дама", 10),
    KING("Король", 10),
    ACE("Туз", 11);

    private final String rusName;
    private final int numeric;

    /**
     * Конструктор ранга карты.
     *
     * @param rusName русское название
     * @param numeric числовое значение ранга
     */
    Rank(String rusName, int numeric) {
        this.rusName = rusName;
        this.numeric = numeric;
    }

    /**
     * Возвращает русское название карты.
     *
     * @return название карты
     */
    public String getRusName() { return rusName; }

    /**
     * Возвращает числовое значение карты.
     *
     * @return значение (2..11)
     */
    public int getNumeric() { return numeric; }
}
