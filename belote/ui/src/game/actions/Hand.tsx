import { CardUI } from "../base/CardUI";
import { Card, TrumpInfos, equalCards, newSortCards } from "../base/domain.d";

type HandProps = {
    cards: Card[],
    uuid: string,
    playableCards: Card[],
    playCard: (uuid: string, card: Card) => void,
    trump?: TrumpInfos | null
}

export const Hand = ({ cards, uuid, playableCards, playCard, trump }: HandProps) => {

    const getAction = (card: Card) => playableCards?.some(c => equalCards(c, card))
        ? () => playCard(uuid, card) : null

    const list = newSortCards(cards, trump).map((card, index) =>
        <CardUI key={index} card={card} trump={card.color == trump?.chosenColor} onClick={getAction(card)} />)

    return (
        <div className="card-container">
            {list}
        </div>
    )
}