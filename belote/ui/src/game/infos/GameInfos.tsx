import { PlayerCard } from "../base/PlayerCard"
import { Player, Trick, equalPlayers } from "../base/domain.d"


export type TrumpInfos = {
    player: Player
    chosenColor: string
}

export type InfosProps = {
    trump?: TrumpInfos | null
    lastTrick?: Trick | null
}

export const Infos = ({ trump, lastTrick }: InfosProps) => {

    const listLastTrick = lastTrick && lastTrick.cards
        .map((cardAndPlayer, index) => <PlayerCard
            key={index} {...cardAndPlayer}
            winner={lastTrick.winner && equalPlayers(cardAndPlayer.player, lastTrick.winner)} />)

    return (
        <div id="infos">
            <div>
                <p>Atout</p>
                {trump && <PlayerCard
                    player={trump.player}
                    card={{ color: trump.chosenColor, figure: trump.chosenColor }} />}
            </div>
            <div>
                <p>Dernier pli</p>
                <div className="card-container card-container-small">
                    {listLastTrick}
                </div>
            </div>
        </div>

    )
}