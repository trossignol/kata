package fr.rossi.belote.server.message.in;

import lombok.Setter;

@Setter
public abstract sealed class InMessage permits StartGame, TrumpChoice, CardToPlay {

    private String playerId;
    private String gameId;
    private String uuid;

    public String playerId() {
        return this.playerId;
    }

    public String gameId() {
        return this.gameId;
    }

    public String uuid() {
        return this.uuid;
    }
}
