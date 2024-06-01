package fr.rossi.belote.server;


import fr.rossi.belote.core.exception.TechnicalException;
import fr.rossi.belote.server.message.in.InMessage;
import fr.rossi.belote.server.message.out.OutMessage;
import fr.rossi.belote.server.serializer.MessageSerializer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint("/socket/game/{username}")
@ApplicationScoped
public class GameSocket {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    MessageSerializer serializer;

    @Inject
    GameService service;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        log.info("New connection user={}", username);
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

    private void closeSession(Session session, String username) {
        silentClose(sessions.remove(username));
        silentClose(session);
    }

    @SneakyThrows
    private void silentClose(Session session) {
        if (session != null && session.isOpen()) session.close();
    }

    @OnMessage
    public void onMessage(String json, @PathParam("username") String username) {
        log.info("New message from user=" + username + " message=" + json);
        try {
            final InMessage message = this.serializer.read(json);
            this.service.handleMessage(username, message);
        } catch (Exception e) {
            log.error("Error handling message for user=" + username, e);
            throw new TechnicalException("Error handling message for user=" + username, e);
        }
    }

    public void send(String username, OutMessage message) {
        var session = sessions.get(username);
        var jsonMessage = this.serializer.write(message);
        log.info("Send message to={} message={}", username, jsonMessage);
        TechnicalException.assertNotNull("Session not found for user=" + username, session);
        session.getAsyncRemote().sendObject(jsonMessage, result -> TechnicalException.assertNull(
                "Error sending message to user=" + username, result.getException()));
    }
}
