package ru.nsu.vorona.Task_1_1_2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void defaultDeckHas52CardsSingleDeck() {
        Deck d = new Deck(1);
        assertEquals(52, d.remaining());
    }

    @Test
    void multipleDecksComposeCorrectly() {
        Deck d = new Deck(3);
        assertEquals(52 * 3, d.remaining());
    }

    @Test
    void drawReducesCount() {
        Deck d = new Deck(1);
        Card c = d.draw();
        assertEquals(51, d.remaining());
        assertNotNull(c);
    }
}

