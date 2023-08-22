package fr.rossi.belote.core.domain;

import fr.rossi.belote.core.utils.Params;

import java.util.ArrayList;

import static java.util.stream.Collectors.joining;

public class CardsAndPlayers extends ArrayList<CardAndPlayer> {

    public CardsAndPlayers() {
        super(Params.NB_PLAYERS);
    }

    @Override
    public String toString() {
        return this.stream()
                .map(CardAndPlayer::toString)
                .collect(joining(" / "));
    }
}
