package ru.nsu.vorona.Task_1_1_2;

/**
 * Класс представляет одну карту в игре Блэкджек.
 */
public class Card {
    private final Rank rank;
    private final Suit suit;

    /**
     * Создаёт карту с заданным рангом и мастью.
     *
     * @param rank ранг карты
     * @param suit масть карты
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /** @return ранг карты */
    public Rank getRank() { return rank; }

    /** @return масть карты */
    public Suit getSuit() { return suit; }

    /**
     * Возвращает базовое значение карты.
     * Туз оценивается как 11, остальные карты по номиналу, а картинки — 10.
     * @return числовое значение карты
     */
    public int baseValue() {
        switch (rank) {
            case ACE: return 11;
            case KING:
            case QUEEN:
            case JACK:
                return 10;
            default:
                return rank.getNumeric();
        }
    }

    /**
     * Возвращает строковое представление карты на русском.
     * @return строка вида "Дама Пики"
     */
    @Override
    public String toString() {
        return rank.getRusName() + " " + suit.getRusName();
    }
}
