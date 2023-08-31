package fr.rossi.belote.server.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.rossi.belote.core.card.Card;
import fr.rossi.belote.core.card.Color;
import fr.rossi.belote.core.card.Figure;

import java.io.IOException;

public class CardDeserializer extends StdDeserializer<Card> {

    public CardDeserializer() {
        super(Card.class);
    }

    @Override
    public Card deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        final JsonNode node = jp.getCodec().readTree(jp);
        return new Card(
                Figure.valueOf(node.get("figure").asText()),
                Color.valueOf(node.get("color").asText()));
    }
}
