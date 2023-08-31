import { socketHandler } from "./socket";

const socketHandler = socketHandler;

const GameStatus = {
    NOT_CONNECTED: 0,
    TO_START: 1,
    CHOOSE_TRUMP_1: 2,
    CHOOSE_TRUMP_2: 3
}

const game = {
    status: GameStatus.NOT_CONNECTED,

    init: function () {
        $("#connect").click(() => this.connect());
        $("#start").click(() => this.start());

        $("#name").keypress((event) => {
            if (event.keyCode == 13 || event.which == 13) this.connect();
        });

        $("#name").focus();
    },

    connect: function () {
        socketHandler.connect()
    }
}