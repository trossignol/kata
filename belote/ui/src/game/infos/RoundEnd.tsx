import { Player, Team } from "../base/domain.d"
import { ScoreTeam } from "./GameInfos"

export type RoundEndProps = {
    player: Player,
    winner: Team,
    status: string,
    tableScores: any,
    runScores: any
}

export const RoundEnd = ({ player, winner, status, tableScores, runScores }: RoundEndProps) => {
    var playerWin = winner.name == player.team

    var tableScoreTeam = new ScoreTeam(player, tableScores)
    var runScoreTeam = new ScoreTeam(player, runScores)

    var message = "TODO"
    switch (status) {
        case "IN":
            message = playerWin
                ? `Ils sont dedans ! (${tableScoreTeam.get(false)} / ${tableScoreTeam.get(true)})`
                : `Vous êtes dedans ! (${tableScoreTeam.get(true)} / ${tableScoreTeam.get(false)})`
            break;
        case "SHUTOUT":
            message = playerWin
                ? `Ils sont capots !`
                : `Vous êtes capots !`
            break;
        default:
            message = `Contrat rempli : ` + (playerWin ? `vous gagnez !` : `ils gagnent !`)

    }
    return <>
        <p>{message}</p>
        <p>Vous marquez {runScoreTeam.get(true)} pts, eux marquent {runScoreTeam.get(false)} pts</p>
    </>
}