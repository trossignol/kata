package fr.rossi.belote.core.player.langchain;

import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.Event;
import fr.rossi.belote.core.player.manual.ManualPlayer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class LangChainPlayer extends ManualPlayer {

    private final RoundRecord roundRecord;

    public LangChainPlayer(String name) {
        super(name);
        this.roundRecord = new RoundRecord(this, this::hand);
    }

    @Override
    public void consume(Event event) {
        super.consume(event);
        this.roundRecord.addEvent(event);
    }

    @Override
    public Optional<Color> chooseTrump(Card card, boolean firstRun) {
        this.askAI(
                String.format("La carte retournée est le %s", card),
                String.format("Ma main est la suivante: %s", this.hand()),
                firstRun ? "Est-ce que je dois prendre ? Réponds 'oui' ou 'non'"
                        : "Personne n'a pris en 'une': est-ce que je dois prendre et si oui à quelle couleur ? Réponds 'non' ou la couleur"
        );

        return super.chooseTrump(card, firstRun);
    }

    @Override
    protected Card cardToPlay(Trick trick, Collection<Card> playableCards) {
        this.askAI(
                trick.cardsAndPlayers().isEmpty()
                        ? "C'est à moi de poser la 1ère carte"
                        : String.format("Voici les cartes sur la table %s", trick.cardsAndPlayers()),
                String.format("Voici les cartes que je peux jouer : %s", playableCards),
                "Quelle carte dois-je jouer ? Réponds en donnant juste la carte dans mon format"
        );
        return super.cardToPlay(trick, playableCards);
    }

    private void askAI(String... events) {
        var context = new ArrayList<>(this.roundRecord);
        Arrays.stream(events).forEach(context::add);
        var prompt = String.join(System.lineSeparator(), context);
        log.info(prompt);

        var model = OpenAiChatModel.withApiKey("");
        String answer = model.generate(prompt);
        log.info(answer);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
