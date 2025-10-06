package ru.nsu.vorona;

public class Card {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }

    // Базовое значение карты (туз здесь как 11; при подсчёте руки мы корректируем)
    public int baseValue() {
        switch (rank) {
            case ACE: return 11;
            case KING:
            case QUEEN:
            case JACK:
                return 10;
            default:
                return rank.getNumeric(); // 2..10
        }
    }

    @Override
    public String toString() {
        return rank.getRusName() + " " + suit.getRusName();
    }
}
