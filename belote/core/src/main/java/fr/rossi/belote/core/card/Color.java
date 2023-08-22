package fr.rossi.belote.core.card;

import java.util.Collection;

import static fr.rossi.belote.core.utils.TextColor.colorEmoji;

public enum Color {
    COEUR(colorEmoji("❤")),
    PIQUE(colorEmoji("♠")),
    CARREAU(colorEmoji("♦")),
    TREFLE(colorEmoji("♣"));
    private final String emoji;

    Color(String emoji) {
        this.emoji = emoji;
    }

    public boolean has(Card card) {
        return this == card.color();
    }

    public Collection<Card> filter(Collection<Card> cards) {
        return cards.stream().filter(this::has).toList();
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.emoji;
    }
}