import { Dispatch, SetStateAction } from 'react';
import useWebSocket, { ReadyState } from 'react-use-websocket';

export type SocketParams = {
    name: string,
    playerId?: string | null,
    gameId?: string | null
}

export class GameSocket {

    private params;
    private setParams;
    private socket;
    private ws;
    private handler;

    constructor([params, setParams]: [SocketParams, Dispatch<SetStateAction<SocketParams>>]) {
        this.params = params;
        this.setParams = setParams;

        var wsProtocol = window.location.protocol == "https:" ? "wss" : "ws";
        this.socket = useWebSocket(
            `${wsProtocol}://${window.location.host}/socket/game/${params.name}`,
            {
                shouldReconnect: (_) => true, reconnectInterval: 500, reconnectAttempts: 100,
                onError: console.error
            });
        this.ws = this.socket.getWebSocket();

        this.handler = (message: any) => console.warn("No handler for message:", message);
    }

    onMessage(handler: (message: any) => void) {
        this.handler = handler;
        if (!this.ws) return
        this.ws.onmessage = (event: MessageEvent) => this.handleMessage(event.data);
    }

    handleMessage(data: string) {
        const message = JSON.parse(data);
        console.log(message);

        if (message.type == "GameStarted") {
            this.setParams((p: SocketParams) => {
                return { name: p.name, playerId: message.playerId, gameId: message.gameId };
            });
        } else {
            if (this.params.playerId !== message.playerId) return console.warn("Unexpected playerId");
            if (this.params.gameId !== message.gameId) return console.warn("Unexpected gameId");
        }

        this.handler(message);
    }

    isConnected() { return this.socket.readyState === ReadyState.OPEN; }

    sendMessage(message: any) {
        console.log(this.params.gameId);
        message.playerId = this.params.playerId;
        message.gameId = this.params.gameId;
        console.log(`Send message ${JSON.stringify(message)}`);
        this.socket.sendJsonMessage(message);
    }
}
