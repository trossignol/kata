import { useState } from 'react';
import "./Game.css";
import { GameSocket, SocketParams } from './Websocket';
import { ChooseTrump } from './actions/ChooseTrump';
import { Hand } from './actions/Hand';
import ProgressBar from './base/ProgressBar';
import { Table } from './base/Table';
import { Card, CardAndPlayer, Player, equalCards } from './base/domain.d';
import { WebSocketDebug } from './debug/WebSocketDebug';
import { Infos, InfosProps } from './infos/GameInfos';
import { RoundEnd } from './infos/RoundEnd';

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
    const [roundEnded, setRoundEnded] = useState<any>(null);
    const [socketParams, setSocketParams] = useState<SocketParams>({ name: name })

    const socket = new GameSocket([socketParams, setSocketParams]);

    const startRound = () => {
        setTable([])
        console.log("runScores={}")
        updateInfos("lastTrick", null)
        setCurrentStatus("")
        console.log('Clear hand', hand.length);
        setHand([])
        setRoundEnded(null)
    }

    const start = () => {
        setPlayers([]);
        setInfos({});
        startRound();
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

    const updateInfos = (field: string, value: any): void => {
        setInfos((infos) => ({
            ...infos,
            [field]: value,
        }));
    }

    const updateInfosFromMessage = (message: any): void => {
        switch (message.type) {
            case "GameStarted": return updateInfos("player", message.players.filter((p: Player) => p.name == name)[0])
            case "TrumpChosen": return updateInfos("trump", message);
            case "TrickEnd": return updateInfos("lastTrick", message);
            case "RoundEnd": return updateInfos("lastRound", message);
        }
    }

    socket.onMessage((message: any) => {
        setCurrentStatus(message.type);
        setLastMessage(() => message);
        updateInfosFromMessage(message);

        switch (message.type) {
            case "GameStarted": return setPlayers(message.players);
            case "StartRound": return startRound();
            case "AddCards": return setHand(message.cards);
            case "PlayCard": return setTable(message.table);
            case "TrickEnd": return setTable(message.cards);
            case "RoundEnd": return setRoundEnded(message);
            // case "GameEnd": 
        }
    });

    return (
        <>
            <div id="game">
                <div id="playground">
                    <Table players={players}
                        table={table}
                        winner={lastMessage?.leader || lastMessage?.winner}
                        trumpInfos={infos.trump} />


                    {socket.isConnected()
                        && <Hand cards={hand}
                            uuid={lastMessage?.uuid}
                            playableCards={lastMessage?.playableCards}
                            playCard={playCard}
                            trump={infos.trump} />}

                    {currentStatus === "ChooseTrump"
                        && <ChooseTrump {...lastMessage}
                            uuid={lastMessage?.uuid}
                            round={lastMessage?.round}
                            card={lastMessage?.card}
                            callback={sendMessage} />}

                    {roundEnded?.winner && <RoundEnd player={infos.player} {...roundEnded} />}
                </div>
                <div id="right-panel">
                    <div><button onClick={start}>Nouvelle partie</button></div>
                    <Infos {...infos} />
                </div>
            </div>
            <WebSocketDebug socket={socket} socketParams={socketParams} lastMessage={lastMessage} />
        </>
    );
};