package fr.rossi.belote.core.player.langchain;

import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.event.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class RoundRecord extends ArrayList<String> {

    private final Player player;
    private final Supplier<List<Card>> getHand;
    private int trickCount;

    public RoundRecord(Player player, Supplier<List<Card>> getHand) {
        super();
        this.player = player;
        this.getHand = getHand;
        this.trickCount = 0;
    }

    public void clear() {
        super.clear();
        this.trickCount = 0;
    }

    private void add(String str, Object... params) {
        this.add(String.format(str, params));
    }

    public void addEvent(Event event) {
        switch (event) {
            case StartRound e -> {
                this.clear();
                this.add("A la belote, je suis %s et je joue avec %s, %s commence", this.player, this.player.partner(), e.firstPlayer());
            }
            case TrumpChosen e ->
                    this.add("La carte présentée était un %s. %s a pris à %s", e.card(), e.player(), e.chosenColor());
            case RoundStarted _ ->
                    this.add("Au départ, j'avais dans la mains les cartes suivantes: %s", this.getHand.get());
            case TrickEnd e -> this.add("Cartes jouées au tour %d: %s", ++this.trickCount, e.cards());
            default -> log.debug("Not handled event: {}", event);
        }
    }

    public void print() {
        log.info(String.join(System.lineSeparator(), this));
    }
}
