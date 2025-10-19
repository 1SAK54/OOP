package ru.nsu.vorona.Task_1_1_2;

/**
 * Класс, представляющий игрока в Блэкджеке.
 */
public class Player {
    /** Рука игрока. */
    protected final Hand hand = new Hand();

    /** @return объект руки игрока */
    public Hand getHand() { return hand; }
}
