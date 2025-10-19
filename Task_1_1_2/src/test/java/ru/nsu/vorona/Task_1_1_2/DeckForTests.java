package ru.nsu.vorona.Task_1_1_2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DeckForTests extends Deck {
    private final Queue<Card> cards;

    public DeckForTests(List<Card> sequence) {
        super(1);
        this.cards = new LinkedList<>(sequence);
    }

    @Override
    public Card draw() {
        return cards.poll();
    }
}
