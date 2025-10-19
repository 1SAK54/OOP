package ru.nsu.vorona.Task_1_1_2;

import java.util.*;

/**
 * Класс представляет руку игрока или дилера.
 * Позволяет добавлять карты и вычислять значение руки с учётом тузов.
 */
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    /** Добавляет карту в руку. */
    public void add(Card c) { cards.add(c); }

    /** @return список карт в руке */
    public List<Card> getCards() { return cards; }

    /**
     * Возвращает наилучшую сумму очков (меньше или равно 21, если возможно).
     *
     * @return значение руки
     */
    public int getBestValue() {
        int total = 0, aces = 0;
        for (Card c : cards) {
            total += c.baseValue();
            if (c.getRank() == Rank.ACE) aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    /** @return true, если сумма очков > 21 */
    public boolean isBust() { return getBestValue() > 21; }

    /** @return true, если рука — блэкджек (две карты и сумма = 21) */
    public boolean isBlackjack() {
        return cards.size() == 2 && getBestValue() == 21;
    }

    /**
     * Возвращает список значений для отображения.
     * Например, туз может быть (11) или (1).
     */
    public List<Integer> getCardValuesForDisplay() {
        List<Integer> vals = new ArrayList<>();
        int total = 0;
        int aces = 0;
        for (Card c : cards) {
            int v = c.baseValue();
            vals.add(v);
            total += v;
            if (c.getRank() == Rank.ACE) aces++;
        }
        for (int i = 0; total > 21 && i < vals.size(); i++) {
            if (vals.get(i) == 11) {
                vals.set(i, 1);
                total -= 10;
            }
        }
        return vals;
    }

    /**
     * Формирует строковое представление руки (например, для вывода на экран).
     *
     * @param hideSecondCard если true — скрывает вторую карту (для дилера)
     * @return текстовое представление руки
     */
    public String toDisplayString(boolean hideSecondCard) {
        StringBuilder sb = new StringBuilder("[");
        List<Integer> displayVals = getCardValuesForDisplay();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) sb.append(", ");
            if (hideSecondCard && i == 1) {
                sb.append("<закрытая карта>");
            } else {
                sb.append(cards.get(i).toString());
                sb.append(" (").append(displayVals.get(i)).append(")");
            }
        }
        sb.append("]");
        if (!hideSecondCard) {
            sb.append(" > ").append(getBestValue());
        }
        return sb.toString();
    }
}
