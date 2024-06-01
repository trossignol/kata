import { CardUI } from "./CardUI"
import { Color } from "./Color"
import "./Display.css"
import { Card, Player } from "./domain.d"

type PlayerCardProps = {
    player?: Player | null,
    card?: Card | null,
    winner?: boolean | null
    trump?: string | null
}

export const PlayerCard = ({ player, card, winner, trump }: PlayerCardProps) => {

    const className = `player-card ${winner ? "player-winner" : ""}`
    return (
        <div className={className}>
            <div className="player-name">{player?.name}<Color color={trump} /></div>
            <CardUI card={card} />
        </div>
    )
}