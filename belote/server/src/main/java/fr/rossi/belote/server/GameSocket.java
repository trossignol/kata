package fr.rossi.belote.server;


import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.server.message.Message;
import fr.rossi.belote.server.message.PlayCard;
import fr.rossi.belote.server.message.StartGame;
import fr.rossi.belote.server.serializer.MessageSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game/{username}")
@ApplicationScoped
public class GameSocket {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    MessageSerializer serializer;

    @Inject
    GameService service;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        this.closeSession(session, username);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        this.closeSession(session, username);
    }

    @SneakyThrows
    private void closeSession(Session session, String username) {
        sessions.remove(username).close();
        session.close();
    }

    @OnMessage
    public void onMessage(String json, @PathParam("username") String username) {
        final Message message = this.serializer.read(json);
        switch (message) {
            case StartGame m -> this.service.startGame(username, m);
            case PlayCard m -> this.service.playCard(username, m);
        }
    }

    private void send(String username, Object message) {
        var session = sessions.get(username);
        var jsonMessage = this.serializer.write(message);
        TechnicalException.assertNotNull("Session not found for user=" + username, session);
        session.getAsyncRemote().sendObject(jsonMessage, result -> TechnicalException.assertNull(
                "Error sending message to user=" + username, result.getException()));
    }
}
