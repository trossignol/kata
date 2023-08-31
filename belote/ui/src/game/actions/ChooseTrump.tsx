import { CardUI } from "../base/CardUI"
import { Card } from "../base/domain.d"

type ChooseTrumpProps = {
    uuid: string,
    round: number,
    card: Card,
    callback: (message: any) => void
}

export const ChooseTrump = ({ uuid, round, card, callback }: ChooseTrumpProps) => {

    const choose = (color: string | null) => callback({
        type: "TrumpChoice",
        uuid: uuid,
        color: color
    })

    const Round1 = () => {
        return (
            <div>
                <button onClick={() => choose(card.color)}>Prendre</button>
                <button onClick={() => choose(null)}>Passer</button>
            </div>
        )
    }
    const Round2 = () => {
        const listColors = ["COEUR", "CARREAU", "PIQUE", "TREFLE"]
            .filter(color => color != card.color)
            .map((color, index) => <CardUI key={index}
                card={{ color: color, figure: color }}
                onClick={() => choose(color)} />)

        return (
            <div className="card-container">
                {listColors}
                <button className="card" onClick={() => choose(null)}>Passer</button>
            </div >
        )
    }

    return (
        <div>
            <h3>Choisir l'atout</h3>
            <div className="card-container"><CardUI card={card} /></div>
            {round == 1 && <Round1 />}
            {round == 2 && <Round2 />}
        </div>
    )
}