package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Класс представляет колоду игральных карт.
 * Позволяет создавать, перемешивать и раздавать карты.
 */
public class Deck {
    private final List<Card> cards;
    private final Random random = new Random();

    /**
     * Создаёт колоду, состоящую из заданного количества стандартных колод (по 52 карты).
     *
     * @param numberOfDecks количество колод
     */
    public Deck(int numberOfDecks) {
        cards = new ArrayList<>();
        for (int d = 0; d < numberOfDecks; d++) {
            for (Suit s : Suit.values()) {
                for (Rank r : Rank.values()) {
                    cards.add(new Card(r, s));
                }
            }
        }
        shuffle();
    }

    /** Перемешивает колоду случайным образом. */
    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    /**
     * Достаёт верхнюю карту из колоды.
     *
     * @return извлечённая карта
     * @throws IllegalStateException если колода пуста
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Колода пуста");
        }
        return cards.remove(cards.size() - 1);
    }

    /** @return количество оставшихся карт в колоде */
    public int remaining() { return cards.size(); }
}
