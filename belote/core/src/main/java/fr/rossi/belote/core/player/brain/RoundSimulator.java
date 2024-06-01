package fr.rossi.belote.core.player.brain;

import fr.rossi.belote.core.domain.Player;
import fr.rossi.belote.core.domain.Trick;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.game.PlayersHandler;
import fr.rossi.belote.core.game.TrickImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RoundSimulator {

    private final Player player;
    private final PlayersHandler players;
    private final TrumpChosen trumpChosen;

    public void simulate(PlayersSimulator simulator) {
        var currentPlayers = players;
        var allTricks = this.buildTricks(currentPlayers.players(), simulator);
        log.info(" >>>>>>>>> {} options", allTricks.size());
    }

    private List<? extends Trick> buildTricks(List<Player> players, PlayersSimulator simulator) {
        var tricks = List.of(new TrickImpl(this.trumpChosen.chosenColor()));

        for (Player p : players) {
            log.info("nb tricks={}", tricks.size());

            // TODO Probability of hand based on played cards
            // TODO Trump or AS first
            var trickSimulator = new TrickSimulator(simulator, this.trumpChosen, this.players.teams(), p);
            tricks = tricks.stream()
                    .flatMap(trick -> trickSimulator.chooseCard(trick)
                            .stream()
                            .map(card -> trick.addCard(p, card))
                    )
                    .limit(50)
                    .toList();
        }

        return tricks;
    }
}
