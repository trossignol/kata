package fr.rossi.belote.player;

import fr.rossi.belote.Params;
import fr.rossi.belote.card.Card;
import fr.rossi.belote.card.Color;
import fr.rossi.belote.domain.Player;
import fr.rossi.belote.domain.Team;
import fr.rossi.belote.domain.Trick;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

import java.security.SecureRandom;
import java.util.*;

import static fr.rossi.belote.exception.TechnicalException.assertTrue;

@Log
@EqualsAndHashCode(of = "name")
public class SimplePlayer implements Player {

    private final String name;
    private final List<Card> hand;
    private Team team;

    public SimplePlayer(String name) {
        super();
        this.name = name;
        this.hand = new ArrayList<>(Card.nbCards() / Params.NB_PLAYERS);
    }

    private static <T> T getRandom(Collection<T> list) {
        return list.stream().toList().get(new SecureRandom().nextInt(list.size()));
    }

    @Override
    public Team team() {
        return this.team;
    }

    @Override
    public void team(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return String.format("Player: %s", this.name);
    }

    @Override
    public List<Card> hand() {
        return this.hand;
    }

    @Override
    public void clearHand() {
        this.hand.clear();
    }

    @Override
    public void addCards(List<Card> cards) {
        assertTrue(String.format("Hand size should be lower than %d (actual size=%d / added cards=%d)",
                        Card.nbCards() / Params.NB_PLAYERS, this.hand.size(), cards.size()),
                this.hand.size() + cards.size() <= Card.nbCards() / Params.NB_PLAYERS);
        this.hand.addAll(cards);
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) {
        // TODO Random choose
        if (new SecureRandom().nextInt(10) != 0) {
            return Optional.empty();
        }

        if (firstRun) {
            return Optional.of(card.color());
        }
        var colors = Arrays.stream(Color.values())
                .filter(color -> color != card.color())
                .filter(color -> this.hand.stream().anyMatch(c -> c.color() == color))
                .toList();
        return colors.isEmpty() ? Optional.empty() : Optional.of(getRandom(colors));
    }

    @Override
    public Card play(Trick trick) {
        var playableCards = trick.playableCards(this.hand);
        // TODO Random choose
        var card = getRandom(playableCards);
        this.hand.remove(card);
        return card;
    }
}

