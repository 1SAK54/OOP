package ru.nsu.vorona.Task_1_1_2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandTest {
    @Test
    void singleAceCountsAs11UnlessBust() {
        Hand h = new Hand();
        h.add(new Card(Rank.ACE, Suit.SPADES));
        h.add(new Card(Rank.SIX, Suit.HEARTS));
        assertEquals(17, h.getBestValue());
    }

    @Test
    void aceBecomesOneWhenBustOccurs() {
        Hand h = new Hand();
        h.add(new Card(Rank.ACE, Suit.SPADES));
        h.add(new Card(Rank.KING, Suit.CLUBS));
        h.add(new Card(Rank.TWO, Suit.DIAMONDS));
        // 11 + 10 + 2 = 23 -> ace becomes 1 -> 1+10+2 = 13
        assertEquals(13, h.getBestValue());
    }

    @Test
    void multipleAcesHandledCorrectly() {
        Hand h = new Hand();
        h.add(new Card(Rank.ACE, Suit.SPADES));
        h.add(new Card(Rank.ACE, Suit.HEARTS));
        h.add(new Card(Rank.NINE, Suit.DIAMONDS));
        // best: 11 + 1 + 9 = 21
        assertEquals(21, h.getBestValue());
    }
}
