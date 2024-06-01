export type Card = {
    color: string
    figure: string
}

const distinct = function (value: any, index: number, array: any[]): boolean {
    return array.indexOf(value) === index;
}

const otherColor = function (colors: string[], color: string): string {
    return colors[0] == color ? colors[1] : colors[0]
}

export const newSortCards = function (cards: Card[], trump?: TrumpInfos | null): Card[] {
    var sortedColors = getSortedColors(cards.map(c => c.color).filter(distinct), trump)

    return cards.sort((c1, c2) => {
        var colorSort = c1.color == c2.color ? 0
            : (sortedColors.indexOf(c1.color) < sortedColors.indexOf(c2.color) ? -1 : 1);

        if (colorSort != 0) return colorSort;
        var figures = trump?.chosenColor == c1.color
            ? ["VALET", "NEUF", "AS", "DIX", "ROI", "DAME", "HUIT", "SEPT"]
            : ["AS", "DIX", "ROI", "DAME", "VALET", "NEUF", "HUIT", "SEPT"]
        return figures.indexOf(c1.figure) < figures.indexOf(c2.figure) ? -1 : 1;
    })
}

export const getSortedColors = function (cardsColors: string[], trump?: TrumpInfos | null): string[] {
    var reds = ["COEUR", "CARREAU"];
    var blacks = ["PIQUE", "TREFLE"];

    var firstColor = trump?.chosenColor && cardsColors.includes(trump.chosenColor)
        ? trump.chosenColor
        : (cardsColors.includes(reds[0]) && cardsColors.includes(reds[1]) ? reds[0] : blacks[0]);
    var sorted: string[] = [];
    sorted.push(firstColor);

    var firstColors = reds.includes(firstColor) ? reds : blacks;
    var secondColors = reds.includes(firstColor) ? blacks : reds;

    var secondColor = cardsColors.includes(secondColors[0]) ? secondColors[0] : secondColors[1]
    sorted.push(secondColor)

    sorted.push(otherColor(firstColors, firstColor))
    sorted.push(otherColor(secondColors, secondColor))


    return sorted;
}

export const equalCards = function (c1: Card, c2: Card): boolean {
    return c1.color == c2.color && c1.figure == c2.figure;
}

export type Player = {
    name: string
    team: string
}

export type Team = {
    name: string
}

export const equalPlayers = function (p1: Player, p2: Player): boolean {
    return p1.name == p2.name && p1.team == p2.team;
}

export type CardAndPlayer = {
    player: Player
    card: Card
}

export type Trick = {
    cards: CardAndPlayer[]
    winner: Player
    runScores: any
}

export type Round = {
    winner: Team
    status: string
    runScores: any
    scores: any
}

export type TrumpInfos = {
    player: Player
    chosenColor: string
}