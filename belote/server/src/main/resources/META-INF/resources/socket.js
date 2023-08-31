const socketHandler = {
    init: function () {
        $("#connect").click(() => this.connect());
        $("#start").click(() => this.start());

        $("#name").keypress((event) => {
            if (event.keyCode == 13 || event.which == 13) this.connect();
        });

        $("#name").focus();
    },

    connect: function (ping = true) {
        var name = $("#name").val();
        console.log(`ws://${location.host}/game/${name}`)
        this.socket = new WebSocket(`ws://${location.host}/game/${name}`);

        this.socket.onopen = () => {
            this.connected(true);
            if (ping) this.ping()
        }
        this.socket.onmessage = (m) => console.log("Got message: " + m.data);
        this.socket.ondisconnect = () => this.reconnect();
    },

    reconnect: function () {
        console.error("Disconnected => Try to reconnect")
        this.connected(false);
        this.connect(false);
    },

    isConnected: function () {
        return this.socket.readyState == WebSocket.OPEN;
    },

    connected: function (conOk) {
        if (conOk) console.log("Connected to the web socket");
        $("#start").attr("disabled", !conOk);
        $("#connect").attr("disabled", conOk);
        $("#name").attr("disabled", conOk);
    },

    ping: function () {
        $.get("/game", data => { })
            .fail(() => console.error("http ping ko"))
        if (!this.isConnected()) this.reconnect();
        setTimeout(() => this.ping(), 1_000);
    },

    start: function () {
        if (this.isConnected()) {
            var value = JSON.stringify({ type: "START_GAME" });
            console.log("Sending: ", value);
            this.socket.send(value);
        } else {
            console.error('Not connected!')
        }
    }
}