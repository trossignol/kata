package fr.rossi.game2048.players.ai;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move;
import fr.rossi.game2048.Move.Direction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DirectionNode {
    private final Grid grid;
    @Getter
    private final Direction direction;

    private List<SetValueNode> children;
    private Optional<Double> score = Optional.empty();

    List<DirectionNode> buildChildren(Map<Grid, SetValueNode> nodesByGrid) {
        final List<DirectionNode> subNodes = new ArrayList<>();
        this.children = simulateValues(grid).stream()
                .map(g -> nodesByGrid.computeIfAbsent(g, ng -> {
                    var directionNodes = simulateDirections(g);
                    subNodes.addAll(directionNodes);
                    return new SetValueNode(g, directionNodes);
                }))
                .toList();
        return subNodes;
    }

    static List<DirectionNode> simulateDirections(Grid grid) {
        return stream(Direction.values())
                .map(d -> new Move(d, grid).apply().map(g -> new DirectionNode(g, d)))
                .filter(Optional::isPresent).map(Optional::get)
                .toList();
    }

    static List<Grid> simulateValues(Grid grid) {
        return grid.getEmpty().stream()
                .flatMap(coord -> stream(Grid.NEW_VALUES)
                        .map(value -> copyAndSet(grid.getData(), coord, value)))
                .map(Grid::new)
                .collect(Collectors.toList());
    }

    private static Integer[][] copyAndSet(Integer[][] inData, Grid.Coord coord, int value) {
        var newData = stream(inData).map(ArrayUtils::clone).toArray(Integer[][]::new);
        newData[coord.row()][coord.col()] = Integer.valueOf(value);
        return newData;
    }

    double evaluateScore() {
        if (this.score.isPresent())
            return this.score.get();

        if (this.children == null || this.children.isEmpty())
            return GridScorer.scoreGrid(this.grid);

        return this.children.stream()
                .mapToDouble(SetValueNode::getScore)
                // TODO Better than average ?
                .average().orElseThrow();
    }

    void resetScore() {
        this.score = Optional.empty();
    }
}