package fr.rossi.belote.card;

import java.util.Collection;

public enum Color {
    COEUR, CARREAU, PIQUE, TREFLE;

    public boolean has(Card card) {
        return this == card.color();
    }

    public Collection<Card> filter(Collection<Card> cards) {
        return cards.stream().filter(this::has).toList();
    }
}