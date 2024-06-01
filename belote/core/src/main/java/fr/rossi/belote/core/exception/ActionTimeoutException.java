package fr.rossi.belote.core.exception;

import lombok.Getter;

@Getter
public class ActionTimeoutException extends Exception {

    private final String action;
    private final String playerName;

    public ActionTimeoutException(String action, String playerName) {
        super(String.format("Timeout waiting for action=%s and player=%s", action, playerName));
        this.action = action;
        this.playerName = playerName;
    }
}
