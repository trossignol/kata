import { Color } from "./Color"
import "./Display.css"
import { Card } from "./domain.d"

type CardUIProps = {
    card?: Card | null,
    onClick?: ((card: Card) => void) | null,
    trump?: boolean | null
}

export const CardUI = ({ card, onClick, trump }: CardUIProps) => {

    const playable = card && onClick
    const click = () => playable && onClick(card)
    const className = `card ${playable ? "card-playable" : ""} ${trump ? "card-trump" : ""}`
    return (
        <div className={className}>
            {card && <button onClick={click}>
                {card.figure}<br />
                <Color color={card?.color} />
            </button>}
        </div>
    )
}