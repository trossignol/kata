import { GameSocket, SocketParams } from "../Websocket";
import { CallApi } from "./CallApi";
import "./Debug.css";

type WebSocketDebugProps = {
    socket: GameSocket
    lastMessage: any
    socketParams: SocketParams

}

export const WebSocketDebug = ({ socket, lastMessage, socketParams }: WebSocketDebugProps) => {

    const trickEnd = {
        "type": "TrickEnd",
        "uuid": null,
        "cards": [
            {
                "card": {
                    "figure": "AS",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Thomas"
                }
            },
            {
                "card": {
                    "figure": "DIX",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Opponent 1"
                }
            },
            {
                "card": {
                    "figure": "DAME",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Partner"
                }
            },
            {
                "card": {
                    "figure": "ROI",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Opponent 2"
                }
            }
        ],
        "winner": {
            "name": "Thomas"
        },
        "runScores": {
            "Team 1": 28,
            "Team 2": 0
        }
    }

    const roundEnd = {
        "type": "RoundEnd",
        "winner": {
            "id": 2,
            "name": "Team 2",
            "players": [
                {
                    "name": "Opponent 1",
                    "team": "Team 2"
                },
                {
                    "name": "Opponent 2",
                    "team": "Team 2"
                }
            ]
        },
        "runScores": {
            "Team 1": 0,
            "Team 2": 182
        },
        "scores": {
            "Team 1": 0,
            "Team 2": 182
        },
        "tableScores": {
            "Team 1": 85,
            "Team 2": 97
        },
        "status": "SIMPLE"
    }

    const roundStart = { type: 'StartRound' }

    const playCard = {
        "table": [
            {
                "card": {
                    "figure": "DAME",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Partner",
                    "team": "Team 1"
                }
            },
            {
                "card": {
                    "figure": "AS",
                    "color": "PIQUE"
                },
                "player": {
                    "name": "Opponent 2",
                    "team": "Team 2"
                }
            }
        ],
        "playableCards": [
            {
                "figure": "ROI",
                "color": "PIQUE"
            }
        ],
        "leader": {
            "name": "Opponent 2",
            "team": "Team 2"
        },
        "type": "PlayCard"
    }

    const sendMock = function (mock: any) {
        mock.playerId = socketParams.playerId;
        mock.gameId = socketParams.gameId;
        socket.handleMessage(JSON.stringify(mock));
    }

    return (
        <div id="debug">
            <div id="debug-actions">
                <button onClick={() => sendMock(roundStart)}>Round start</button><br />
                <button onClick={() => sendMock(playCard)}>Play card</button><br />
                <button onClick={() => sendMock(trickEnd)}>Trick end</button><br />
                <button onClick={() => sendMock(roundEnd)}>Round end</button><br />
                <CallApi />
            </div>
            <div id="debug-log">
                <p> WebSocket {socket.isConnected() ? "OK" : "ERROR"}</p>
                {lastMessage ? <p>Last message: {JSON.stringify(lastMessage)}</p> : null}
            </div>
        </div >
    );
}