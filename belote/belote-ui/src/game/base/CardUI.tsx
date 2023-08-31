import "./Display.css"
import { Card } from "./domain.d"

type CardUIProps = {
    card?: Card | null,
    onClick?: ((card: Card) => void) | null,
    played?: boolean | null
}

type ColorParams = {
    color: string,
    emoji: string
}

export const CardUI = ({ card, onClick, played }: CardUIProps) => {

    const colors = new Map<String, ColorParams>([
        ["COEUR", { color: "red", emoji: "❤" }],
        ["CARREAU", { color: "red", emoji: "♦" }],
        ["PIQUE", { color: "black", emoji: "♠" }],
        ["TREFLE", { color: "black", emoji: "♣" }]
    ]);

    const { color, emoji } = colors.get(card?.color || "") || { color: "black", emoji: "" };
    const style = { color: color, fontSize: "2em" };

    const playable = card && !played && onClick
    const click = () => playable && onClick(card)
    const className = `card ${playable ? "card-playable" : ""} ${played ? "card-played" : ""}`
    return (
        <div className={className}>
            {card && <button onClick={click}>
                {card.figure}<br />
                <p style={style}>{emoji}</p>
            </button>}
        </div>
    )
}