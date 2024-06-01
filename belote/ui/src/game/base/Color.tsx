type ColorParams = {
    color: string,
    emoji: string
}

const COLORS = new Map<String, ColorParams>([
    ["COEUR", { color: "red", emoji: "❤" }],
    ["CARREAU", { color: "red", emoji: "♦" }],
    ["PIQUE", { color: "black", emoji: "♠" }],
    ["TREFLE", { color: "black", emoji: "♣" }]
]);

type ColorProps = {
    color?: string | null
}

export const Color = (props: ColorProps) => {

    const { color, emoji } = COLORS.get(props.color || "") || { color: "black", emoji: "" };
    const style = { color: color };
    return (
        <div className="card-color" style={style}>{emoji}</div>
    )
}