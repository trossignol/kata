import { PlayerCard } from "./PlayerCard"
import { CardAndPlayer, Player, TrumpInfos, equalPlayers } from "./domain.d"

type TableProps = {
    players: Player[]
    table: CardAndPlayer[]
    winner?: Player | null
    trumpInfos?: TrumpInfos | null
}

export const Table = ({ players, table, winner, trumpInfos }: TableProps) => {

    const list = players
        .map((player, index) => <PlayerCard
            key={index}
            player={player}
            trump={trumpInfos && equalPlayers(player, trumpInfos?.player) ? trumpInfos.chosenColor : null}
            winner={winner && equalPlayers(player, winner)}
            card={table.filter(c => equalPlayers(c.player, player))[0]?.card} />)

    return (
        <div className="table card-container">
            {list}
        </div>
    )
}