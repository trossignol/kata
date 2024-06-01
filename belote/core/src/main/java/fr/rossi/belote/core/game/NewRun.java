package fr.rossi.belote.core.game;

import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.Trick;

import java.util.Collection;

public class NewRun {
    private final WonTricks wonTricks;

    public NewRun(Collection<Team> teams) {
        super();
        this.wonTricks = new WonTricks(teams);
    }

    private NewRun(NewRun source, Trick trick) {
        super();
        this.wonTricks = source.wonTricks.add(trick);
    }

    public NewRun add(Trick trick) {
        return new NewRun(this, trick);
    }
}
