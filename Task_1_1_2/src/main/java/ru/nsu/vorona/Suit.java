package ru.nsu.vorona;

public enum Suit {
    SPADES("Пики"),
    HEARTS("Червы"),
    CLUBS("Трефы"),
    DIAMONDS("Бубны");

    private final String rusName;
    Suit(String rusName) { this.rusName = rusName; }
    public String getRusName() { return rusName; }
}
