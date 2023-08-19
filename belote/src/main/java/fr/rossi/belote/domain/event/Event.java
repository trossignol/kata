package fr.rossi.belote.domain.event;

public sealed interface Event permits ChooseTrump, TrumpChosen, TrickEnd, RoundEnd {
}