package ru.nsu.vorona;

/**
 * Класс, представляющий игрока в Блэкджеке.
 */
public class Player {
    /** Рука игрока. */
    protected final Hand hand = new Hand();

    /** @return объект руки игрока */
    public Hand getHand() { return hand; }
}
