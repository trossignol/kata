package fr.rossi.game2048.players.ai;

import java.util.List;

import fr.rossi.game2048.Grid;

record SetValueNode(Grid grid, List<DirectionNode> children) {
    public double getScore() {
        return this.children.stream().mapToDouble(DirectionNode::evaluateScore).max()
                .orElseGet(() -> GridScorer.scoreGrid(this.grid));
    }
}
