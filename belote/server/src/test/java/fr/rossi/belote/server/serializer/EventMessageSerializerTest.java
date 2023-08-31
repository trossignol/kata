package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;
import fr.rossi.belote.core.domain.Team;
import fr.rossi.belote.core.domain.event.RoundEnd;
import fr.rossi.belote.core.domain.event.TrumpChosen;
import fr.rossi.belote.core.player.SimplePlayer;
import fr.rossi.belote.server.message.out.EventMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class EventMessageSerializerTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        new JacksonConfig().customize(jsonMapper);
    }

    private static void assertEqualsMap(Object expected, Object value) {
        assertEquals(jsonMapper.convertValue(expected, Map.class), value);
    }

    @Test
    void testSerializeTrumpChosen() throws JsonProcessingException {
        var card = new Card(Figure.ROI, Color.COEUR);
        var player = new SimplePlayer("player-name");
        var chosenColor = Color.PIQUE;

        var event = new TrumpChosen(card, player, chosenColor);

        var playerId = UUID.randomUUID().toString();
        var gameId = UUID.randomUUID().toString();
        var uuid = UUID.randomUUID().toString();

        var message = new EventMessage(event).playerId(playerId).gameId(gameId).uuid(uuid);

        var json = jsonMapper.convertValue(message, Map.class);
        System.out.println(jsonMapper.writeValueAsString(message));
        System.out.println(json);

        assertEquals(event.getClass().getSimpleName(), json.get("type"));
        assertEqualsMap(card, json.get("card"));
        assertEqualsMap(player, json.get("player"));
        assertEquals(chosenColor.name(), json.get("chosenColor"));
        assertEquals(playerId, json.get("playerId"));
        assertEquals(gameId, json.get("gameId"));
        assertEquals(uuid, json.get("uuid"));
    }


    @Test
    void testSerializeRoundEnded() throws JsonProcessingException {
        var player1 = new SimplePlayer("player-name-1");
        var player2 = new SimplePlayer("player-name-2");

        var winner = new Team(1, List.of(player1, player2));
        var looser = new Team(2, List.of(player1, player2));
        var event = new RoundEnd(winner, RoundEnd.Status.SIMPLE,
                Map.of(winner, 120, looser, 42), Map.of(winner, 162, looser, 0));

        var playerId = UUID.randomUUID().toString();
        var gameId = UUID.randomUUID().toString();
        var uuid = UUID.randomUUID().toString();

        var message = new EventMessage(event).playerId(playerId).gameId(gameId).uuid(uuid);

        var json = jsonMapper.convertValue(message, Map.class);
        System.out.println(jsonMapper.writeValueAsString(message));
        System.out.println(json);

        assertEquals(event.getClass().getSimpleName(), json.get("type"));
        assertEqualsMap(winner, json.get("winner"));
        // assertEquals(chosenColor.name(), json.get("chosenColor"));
        assertEquals(playerId, json.get("playerId"));
        assertEquals(gameId, json.get("gameId"));
        assertEquals(uuid, json.get("uuid"));
    }
}
