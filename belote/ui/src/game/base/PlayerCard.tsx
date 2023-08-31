import { CardUI } from "./CardUI"
import "./Display.css"
import { Card, Player } from "./domain.d"

type PlayerCardProps = {
    player: Player,
    card?: Card | null,
    winner?: boolean | null
}

export const PlayerCard = ({ player, card, winner }: PlayerCardProps) => {

    const className = `player-card ${winner ? "player-winner" : ""}`
    return (
        <div className={className}>
            <p>{player.name}</p>
            <CardUI card={card} />
        </div>
    )
}