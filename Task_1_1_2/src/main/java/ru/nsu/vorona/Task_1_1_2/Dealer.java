package ru.nsu.vorona.Task_1_1_2;

/**
 * Класс, представляющий дилера в Блэкджеке.
 * У дилера есть скрытая карта, открывающаяся позже.
 */
public class Dealer extends Player {
    private Card hiddenCard = null;

    /**
     * Устанавливает скрытую карту дилера.
     *
     * @param c скрытая карта
     */
    public void setHidden(Card c) { hiddenCard = c; hand.add(c); }

    /**
     * Открывает и возвращает скрытую карту.
     *
     * @return открытая карта
     */
    public Card revealHidden() {
        Card c = hiddenCard;
        hiddenCard = null;
        return c;
    }

    /** @return true, если скрытая карта ещё не открыта */
    public boolean hasHidden() { return hiddenCard != null; }
}
