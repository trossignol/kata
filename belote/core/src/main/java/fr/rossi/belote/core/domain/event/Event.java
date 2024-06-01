package fr.rossi.belote.core.domain.event;

public sealed interface Event permits GameStarted, StartRound, RoundStarted,
        ChooseTrump, TrumpChosen, TrickEnd, RoundEnd, GameEnd {
}