export type Card = {
    color: string
    figure: string
}

export const equalCards = function (c1: Card, c2: Card): boolean {
    return c1.color == c2.color && c1.figure == c2.figure;
}

export type Player = {
    name: string
}

export const equalPlayers = function (p1: Player, p2: Player): boolean {
    return p1.name == p2.name;
}

export type CardAndPlayer = {
    player: Player
    card: Card
}

export type Trick = {
    cards: CardAndPlayer[]
    winner: Player
}