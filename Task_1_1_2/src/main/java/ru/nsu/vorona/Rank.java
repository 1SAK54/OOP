package ru.nsu.vorona;

public enum Rank {
    TWO(2, "Двойка"),
    THREE(3, "Тройка"),
    FOUR(4, "Четверка"),
    FIVE(5, "Пятерка"),
    SIX(6, "Шестерка"),
    SEVEN(7, "Семерка"),
    EIGHT(8, "Восьмерка"),
    NINE(9, "Девятка"),
    TEN(10, "Десятка"),
    JACK(10, "Валет"),
    QUEEN(10, "Дама"),
    KING(10, "Король"),
    ACE(11, "Туз");

    private final int numeric;
    private final String rusName;

    Rank(int numeric, String rusName) {
        this.numeric = numeric;
        this.rusName = rusName;
    }

    public int getNumeric() { return numeric; }
    public String getRusName() { return rusName; }
}
