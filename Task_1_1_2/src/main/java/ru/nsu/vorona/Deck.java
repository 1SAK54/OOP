package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;
    private final Random random = new Random();

    // Создает колоду из указанного числа стандартных колод.
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

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    // Вытаскиваем верхнюю карту
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Колода пуста");
        }
        return cards.remove(cards.size() - 1);
    }

    public int remaining() { return cards.size(); }
}
