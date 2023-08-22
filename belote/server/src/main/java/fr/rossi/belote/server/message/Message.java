package fr.rossi.belote.server.message;

public sealed abstract class Message permits StartGame, PlayCard {

    private final Type type;

    protected Message(Type type) {
        super();
        this.type = type;
    }

    public Type type() {
        return this.type;
    }

    public enum Type {
        START_GAME(StartGame.class),
        PLAY_CARD(PlayCard.class);

        private final Class<? extends Message> targetClass;

        Type(Class<? extends Message> targetClass) {
            this.targetClass = targetClass;
        }

        public Class<? extends Message> targetClass() {
            return this.targetClass;
        }
    }
}
