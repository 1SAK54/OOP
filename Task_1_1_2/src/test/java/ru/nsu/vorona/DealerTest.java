package ru.nsu.vorona;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DealerTest {
    @Test
    void dealerStandsOnSoft17() {
        Hand h = new Hand();
        h.add(new Card(Rank.ACE, Suit.SPADES));
        h.add(new Card(Rank.SIX, Suit.HEARTS));
        assertEquals(17, h.getBestValue());
    }
}
