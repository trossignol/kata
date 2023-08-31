import { CardUI } from "../base/CardUI"
import { Card, equalCards } from "../base/domain.d"

type HandProps = {
    cards: Card[],
    uuid: string,
    playableCards: Card[],
    playCard: (uuid: string, card: Card) => void
}

export const Hand = ({ cards, uuid, playableCards, playCard }: HandProps) => {

    const getAction = (card: Card) => playableCards?.some(c => equalCards(c, card))
        ? () => playCard(uuid, card) : null

    const list = cards.map((card, index) =>
        <CardUI key={index} card={card}
            onClick={getAction(card)}
            played={false} />)

    return (
        <div className="card-container">
            {list}
        </div>
    )
}