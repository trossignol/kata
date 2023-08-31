import { PlayerCard } from "./PlayerCard"
import { CardAndPlayer, Player, equalPlayers } from "./domain.d"

type TableProps = {
    players: Player[]
    table: CardAndPlayer[]
    winner?: Player | null
}

export const Table = ({ players, table, winner }: TableProps) => {

    const list = players
        .map((player, index) => <PlayerCard
            key={index}
            player={player}
            winner={winner && equalPlayers(player, winner)}
            card={table.filter(c => equalPlayers(c.player, player))[0]?.card} />)

    return (
        <div className="table card-container">
            {list}
        </div>
    )
}