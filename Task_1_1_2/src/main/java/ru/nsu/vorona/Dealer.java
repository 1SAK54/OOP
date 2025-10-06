package ru.nsu.vorona;

public class Dealer extends Player {
    private Card hiddenCard = null;

    public void setHidden(Card c) { hiddenCard = c; hand.add(c); }

    public Card revealHidden() {
        Card c = hiddenCard;
        hiddenCard = null;
        return c;
    }

    public boolean hasHidden() { return hiddenCard != null; }
}
