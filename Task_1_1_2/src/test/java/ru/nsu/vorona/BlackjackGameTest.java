package ru.nsu.vorona;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.Rank;
import ru.nsu.vorona.Suit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlackjackGameTest {

    @Test
    void playerWinsWithBlackjack() {
        DeckForTests deck = new DeckForTests(List.of(
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.KING, Suit.DIAMONDS),
                new Card(Rank.SEVEN, Suit.CLUBS)
        ));

        BlackjackGame game = new BlackjackGame(deck);
        game.playRoundForTest();

        assertEquals(1, game.getPlayerScore());
        assertEquals(0, game.getDealerScore());
    }

    @Test
    void playerBustsAndLoses() {
        DeckForTests deck = new DeckForTests(List.of(
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.SIX, Suit.CLUBS),
                new Card(Rank.EIGHT, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.FIVE, Suit.CLUBS)
        ));

        BlackjackGame game = new BlackjackGame(deck);
        game.playRoundForTest();

        assertEquals(0, game.getPlayerScore());
        assertEquals(1, game.getDealerScore());
    }


    @Test
    void dealerHitsUntil17() {
        DeckForTests deck = new DeckForTests(List.of(
                new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.FIVE, Suit.HEARTS),
                new Card(Rank.SIX, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.SPADES),
                new Card(Rank.TEN, Suit.CLUBS)
        ));

        BlackjackGame game = new BlackjackGame(deck);
        game.playRoundForTest();

        int dealerValue = game.getDealerHandValue();
        assertTrue(dealerValue >= 17, "Дилер должен добрать до 17 очков");
    }

    @Test
    void tieGame() {
        DeckForTests deck = new DeckForTests(List.of(
                new Card(Rank.TEN, Suit.CLUBS),
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.SEVEN, Suit.DIAMONDS)
        ));

        BlackjackGame game = new BlackjackGame(deck);
        game.playRoundForTest();

        assertEquals(0, game.getPlayerScore(), "При ничьей счёт не должен измениться");
        assertEquals(0, game.getDealerScore(), "При ничьей счёт не должен измениться");
    }

    @Test
    void dealerBustsPlayerWins() {
        DeckForTests deck = new DeckForTests(List.of(
                new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.NINE, Suit.CLUBS),
                new Card(Rank.SEVEN, Suit.DIAMONDS),
                new Card(Rank.SIX, Suit.SPADES),
                new Card(Rank.QUEEN, Suit.HEARTS)
        ));

        BlackjackGame game = new BlackjackGame(deck);
        game.playRoundForTest();

        assertEquals(1, game.getPlayerScore(), "Игрок должен выиграть, если дилер перебрал");
    }
}
