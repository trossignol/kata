package fr.rossi.belote.core.player.brain.trumpchoice;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import lombok.Getter;

import java.util.Collection;

@Getter
class TrumpChoiceParams {
    private final Collection<Card> cards;
    private final Color color;
    private final Collection<Card> trumpCards;
    private final int trumpPoints;
    private final int otherAsCount;
    private final int points;

    protected TrumpChoiceParams(Collection<Card> cards, Color color) {
        super();
        this.cards = cards;
        this.color = color;
        this.trumpCards = this.cards.stream().filter(color::has).toList();
        this.trumpPoints = trumpCards.stream().mapToInt(card -> card.getPoints(color)).sum();
        this.otherAsCount = (int) this.cards.stream()
                .filter(card -> !color.has(card))
                .filter(card -> card.figure() == Figure.AS)
                .count();
        this.points = this.cards.stream()
                .mapToInt(card -> card.getPoints(color))
                .sum();
    }
}