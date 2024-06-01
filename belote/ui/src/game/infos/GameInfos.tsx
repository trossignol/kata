import { PlayerCard } from "../base/PlayerCard"
import { Player, Round, Trick, TrumpInfos, equalPlayers } from "../base/domain.d"
import "./GameInfos.css"

type InfosScoreProps = {
    player?: Player | null
    scores: any
}

export type InfosProps = {
    player?: Player | null
    lastTrick?: Trick | null
    lastRound?: Round | null
    trump?: TrumpInfos | null
}

export class ScoreTeam {
    private player;
    private scores;

    constructor(player?: Player | null, scores?: any | null) {
        this.player = player;
        this.scores = scores;
    }

    get(playerTeam: boolean): string {
        if (!this.player || !this.scores) return "0";
        for (const [team, score] of Object.entries(this.scores)) {
            if (playerTeam == (team == this.player?.team)) return String(score);
        }
        return "0";
    }
}

const InfosScore = ({ player, scores }: InfosScoreProps) => {
    var scoreTeam = new ScoreTeam(player, scores);
    return (
        <div>
            <div>{scoreTeam.get(true)}</div>
            <div>{scoreTeam.get(false)}</div>
        </div>
    )
}

export const Infos = ({ player, lastTrick, lastRound }: InfosProps) => {

    const listLastTrick = lastTrick && lastTrick.cards
        .map((cardAndPlayer, index) => <PlayerCard
            key={index} {...cardAndPlayer}
            winner={lastTrick.winner && equalPlayers(cardAndPlayer.player, lastTrick.winner)} />)

    return (
        <div id="infos">
            <div>
                <p>Scores</p>
                <div id="scores">
                    <div><div>Nous</div><div>Eux</div></div>
                    <InfosScore player={player} scores={lastTrick?.runScores} />
                    <hr />
                    <InfosScore player={player} scores={lastRound?.scores} />
                </div>
            </div>
            {lastTrick && <div>
                <p>Dernier pli</p>
                <div className="card-container card-container-small">
                    {listLastTrick}
                </div>
            </div>}
        </div>

    )
}