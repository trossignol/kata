package fr.rossi.belote.core.player;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.exception.ActionTimeoutException;
import fr.rossi.belote.core.utils.Params;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.*;

import static fr.rossi.belote.core.exception.TechnicalException.assertTrue;

@Slf4j
@EqualsAndHashCode(of = "name")
public class SimplePlayer implements Player {

    private final String name;
    private final List<Card> hand;
    private String gameId;
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
    public String toString() {
        return String.format("P-%S", this.name);
    }

    @Override
    public String name() {
        return this.name;
    }

    protected String gameId() {
        return this.gameId;
    }

    @Override
    public Player gameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    @Override
    public Team team() {
        return this.team;
    }

    @Override
    public Player team(Team team) {
        this.team = team;
        return this;
    }

    public List<Card> hand() {
        return this.hand;
    }

    @Override
    public Player clearHand() {
        this.hand.clear();
        return this;
    }

    @Override
    public Player addCards(List<Card> cards) {
        assertTrue(String.format("Hand size should be lower than %d (actual size=%d / added cards=%d)",
                        Card.nbCards() / Params.NB_PLAYERS, this.hand.size(), cards.size()),
                this.hand.size() + cards.size() <= Card.nbCards() / Params.NB_PLAYERS);
        this.hand.addAll(cards);
        return this;
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) throws ActionTimeoutException {
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
    public final Card play(Trick trick) throws ActionTimeoutException {
        var playableCards = trick.playableCards(this, this.hand);
        var card = this.cardToPlay(trick, playableCards);
        assertTrue("Can't play card=" + card, playableCards.contains(card));
        this.hand.remove(card);
        return card;
    }

    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) throws ActionTimeoutException {
        return getRandom(playableCards);
    }
}

