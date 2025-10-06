package ru.nsu.vorona;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void add(Card c) { cards.add(c); }
    public List<Card> getCards() { return cards; }

    // Возвращает наилучшую сумму <=21 или минимальную сумму >21 (если перебор)
    public int getBestValue() {
        int total = 0;
        int aces = 0;
        for (Card c : cards) {
            total += c.baseValue();
            if (c.getRank() == Rank.ACE) aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10; // делаем один туз =1 вместо 11
            aces--;
        }
        return total;
    }

    public boolean isBust() { return getBestValue() > 21; }
    public boolean isBlackjack() {
        return cards.size() == 2 && getBestValue() == 21;
    }

    // Возвращает список значений для отображения — чтобы показать, например, туз как (1) или (11)
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
        // Снижаем по одному туза (11->1 -> -10) пока >21 и есть тузы
        for (int i = 0; total > 21 && i < vals.size(); i++) {
            if (vals.get(i) == 11) {
                vals.set(i, 1);
                total -= 10;
            }
        }
        // Если после прохода всё ещё >21, оставляем как есть
        return vals;
    }

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
