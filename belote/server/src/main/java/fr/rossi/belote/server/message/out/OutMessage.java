package fr.rossi.belote.server.message.out;

import lombok.Getter;

@Getter
public abstract sealed class OutMessage permits GameStarted, ChooseTrump, PlayCard, AddCards, EventMessage {

    private String playerId;
    private String gameId;
    private String uuid;

    protected OutMessage() {
        super();
    }

    public OutMessage playerId(String playerId) {
        this.playerId = playerId;
        return this;
    }

    public OutMessage gameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public OutMessage uuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }
}
