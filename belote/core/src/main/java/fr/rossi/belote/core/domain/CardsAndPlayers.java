package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.core.utils.Params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class CardsAndPlayers extends ArrayList<CardAndPlayer> {

    public CardsAndPlayers() {
        super(Params.NB_PLAYERS);
    }

    private CardsAndPlayers(CardsAndPlayers source, CardAndPlayer cardAndPlayer) {
        this();
        super.addAll(source);
        super.add(cardAndPlayer);
    }

    private CardsAndPlayers(CardsAndPlayers source, Collection<CardAndPlayer> cardsAndPlayers) {
        this();
        super.addAll(source);
        super.addAll(cardsAndPlayers);
    }

    public static Optional<Card> getWinner(List<Card> cards, Color trumpColor) {
        // TODO Mix with this.getWinner(...)
        var hasTrump = cards.stream().map(Card::color).anyMatch(c -> c == trumpColor);
        var winnerColor = hasTrump ? trumpColor : cards.get(0).color();
        return cards.stream()
                .filter(winnerColor::has)
                .min((c1, c2) -> c1.figure().compareTo(c2.figure(), winnerColor == trumpColor));
    }

    public CardsAndPlayers add(Card card, Player player) {
        return new CardsAndPlayers(this, new CardAndPlayer(card, player));
    }

    public CardsAndPlayers add(Collection<CardAndPlayer> cards) {
        return new CardsAndPlayers(this, cards);
    }

    @Override
    public boolean add(CardAndPlayer c) {
        throw new TechnicalException("Unmodifiable list");
    }

    @Override
    public boolean addAll(Collection<? extends CardAndPlayer> cards) {
        throw new TechnicalException("Unmodifiable list");
    }

    @Override
    public String toString() {
        return this.stream()
                .map(CardAndPlayer::toString)
                .collect(joining(" / "));
    }

    public Optional<Color> wantedColor() {
        return this.isEmpty() ? Optional.empty()
                : Optional.of(this.get(0).card().color());
    }

    public Optional<CardAndPlayer> getWinner(Color trumpColor) {
        if (this.isEmpty()) return Optional.empty();
        var hasTrump = this.stream().map(CardAndPlayer::card).map(Card::color).anyMatch(c -> c == trumpColor);
        var winnerColor = hasTrump ? trumpColor : this.wantedColor().orElseThrow();
        return this.stream()
                .filter(c -> winnerColor.has(c.card()))
                .min((c1, c2) -> c1.card().figure().compareTo(c2.card().figure(), winnerColor == trumpColor));
    }

    public boolean hasPlayer(Player player) {
        return this.stream().map(CardAndPlayer::player).anyMatch(player::equals);
    }
}
