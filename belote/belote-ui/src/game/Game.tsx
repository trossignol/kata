import { useState } from 'react';
import "./Game.css";
import { GameSocket, SocketParams } from './Websocket';
import { ChooseTrump } from './actions/ChooseTrump';
import { Hand } from './actions/Hand';
import { Table } from './base/Table';
import { Card, CardAndPlayer, Player, equalCards } from './base/domain.d';
import { Infos, InfosProps } from './infos/GameInfos';

type GameProps = {
    name: string
}

export const Game = ({ name }: GameProps) => {
    const [infos, setInfos] = useState<InfosProps>({});
    const [currentStatus, setCurrentStatus] = useState("");
    const [lastMessage, setLastMessage] = useState<any>({});
    const [players, setPlayers] = useState<Player[]>([]);
    const [table, setTable] = useState<CardAndPlayer[]>([]);
    const [hand, setHand] = useState<Card[]>([]);

    const socket = new GameSocket(useState<SocketParams>({ name: name }));

    const start = () => {
        setPlayers([])
        setTable([])
        setHand([])
        sendMessage({ type: "StartGame" });
    }

    const sendMessage = (message: any) => {
        setCurrentStatus("");
        socket.sendMessage(message);
    }

    const playCard = (uuid: string, card: Card) => {
        setHand(hand => hand.filter(c => !equalCards(c, card)));
        sendMessage({ type: "CardToPlay", uuid: uuid, card: card });
    }

    socket.onMessage((message: any) => {
        setCurrentStatus(message.type);
        setLastMessage(() => message);
        switch (message.type) {
            case "GameStarted": return setPlayers(message.players);
            case "AddCards": return setHand(() => {
                console.log('Add', hand, message.cards);
                return hand.concat(message.cards);
            });
            case "TrumpChosen": return setInfos(infos => {
                infos.trump = message;
                return infos;
            });
            case "PlayCard": return setTable(message.table);
            case "TrickEnd": {
                setInfos(infos => {
                    infos.lastTrick = message;
                    return infos;
                });
                return setTable(message.cards);
            }
        }
    });

    return (
        <div id="game">
            <div id="playground">
                <button onClick={start}>Start</button>


                <Table players={players} table={table} winner={lastMessage?.winner} />

                {socket.isConnected()
                    && <Hand cards={hand}
                        uuid={lastMessage?.uuid}
                        playableCards={lastMessage?.playableCards}
                        playCard={playCard} />}

                {currentStatus === "ChooseTrump"
                    && <ChooseTrump {...lastMessage}
                        uuid={lastMessage?.uuid}
                        round={lastMessage?.round}
                        card={lastMessage?.card}
                        callback={sendMessage} />}

                <div>
                    <p>WebSocket {socket.isConnected() ? "OK" : "ERROR"}</p>
                    {lastMessage ? <p>Last message: {JSON.stringify(lastMessage)}</p> : null}
                </div>
            </div>
            <Infos {...infos} />
        </div>
    );
};