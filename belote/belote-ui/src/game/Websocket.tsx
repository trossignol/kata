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

    constructor([params, setParams]: [SocketParams, Dispatch<SetStateAction<SocketParams>>]) {
        this.params = params;
        this.setParams = setParams;

        this.socket = useWebSocket(
            `ws://${window.location.host}/socket/game/${params.name}`,
            { shouldReconnect: (_) => true, reconnectInterval: 500, reconnectAttempts: 100 });
        this.ws = this.socket.getWebSocket();
    }

    onMessage(handler: (message: any) => void) {
        if (!this.ws) return
        this.ws.onmessage = (event: MessageEvent) => {
            const message = JSON.parse(event.data);
            console.log(message);

            if (message.type == "GameStarted") {
                this.setParams((p: SocketParams) => {
                    return { name: p.name, playerId: message.playerId, gameId: message.gameId };
                });
            } else {
                if (this.params.playerId !== message.playerId) return console.warn("Unexpected playerId");
                if (this.params.gameId !== message.gameId) return console.warn("Unexpected gameId");
            }

            handler(message);
        }
    }

    isConnected() { return this.socket.readyState === ReadyState.OPEN }

    sendMessage(message: any) {
        console.log(this.params.gameId);
        message.playerId = this.params.playerId;
        message.gameId = this.params.gameId;
        console.log(`Send message ${JSON.stringify(message)}`);
        this.socket.sendJsonMessage(message);
    }
}
