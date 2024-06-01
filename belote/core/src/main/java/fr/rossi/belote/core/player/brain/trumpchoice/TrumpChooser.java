package fr.rossi.belote.core.player.brain.trumpchoice;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class TrumpChooser {
    private final Collection<Card> cards;

    public TrumpChooser(Collection<Card> hand, Card card) {
        super();
        this.cards = new ArrayList<>(hand);
        this.cards.add(card);
    }

    public Optional<TrumpChoice> choose(Color color, double minConfidence) {
        var params = new TrumpChoiceParams(this.cards, color);
        return ChoiceCause.get(params, minConfidence)
                .map(cause -> new TrumpChoice(color, cause, params.getPoints()));
    }
}