package fr.rossi.belote.core.domain.event;

public sealed interface Event permits ChooseTrump, TrumpChosen, TrickEnd, RoundEnd {
}