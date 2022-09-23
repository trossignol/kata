package fr.rossi.game2048.players;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move;
import fr.rossi.game2048.Move.Direction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AIPlayer implements Player {

    private final Map<Grid, SetValueNode> nodesByGrid;

    public AIPlayer() {
        this.nodesByGrid = new ConcurrentHashMap<>();
    }

    @Override
    public Direction getDirection(Grid grid) {
        final List<DirectionNode> rootNodes = Optional.ofNullable(this.nodesByGrid.get(grid))
                .map(SetValueNode::children)
                .orElseGet(() -> simulateDirections(grid));

        nodesByGrid.clear();
        final List<DirectionNode> nodesToBuild = Collections.synchronizedList(new ArrayList<>(rootNodes));
        int nbNodes = grid.getEmpty().size() < 5 ? 10_000 : 1_000;
        do {
            var nodeToBuild = nodesToBuild.remove(0);
            nodesToBuild.addAll(nodeToBuild.buildChildren(this.nodesByGrid));
            nbNodes--;
        } while (nbNodes > 0 && !nodesToBuild.isEmpty());
        rootNodes.forEach(node -> node.buildChildren(this.nodesByGrid));

        return rootNodes.stream()
                .sorted(Comparator.comparing(node -> -node.evaluateScore()))
                .findFirst().map(DirectionNode::getDirection).orElseThrow();
    }

    @Override
    public void close() throws IOException {
        this.nodesByGrid.clear();
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

    private static List<DirectionNode> simulateDirections(Grid grid) {
        return stream(Direction.values())
                .map(d -> new Move(d, grid).apply().map(g -> new DirectionNode(g, d)))
                .filter(Optional::isPresent).map(Optional::get)
                .toList();
    }

    private static double scoreGrid(Grid grid) {
        // TODO Evaluate empty position ?
        var maxValue = stream(grid.getData())
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue).max().orElse(0);

        var empty = grid.getEmpty().size();

        var score = stream(grid.getData())
                .flatMap(Arrays::stream)
                .map(value -> value == null ? maxValue : value)
                .mapToInt(Integer::intValue)
                .mapToDouble(value -> Math.pow(value, 2.0))
                .sum();

        return score;
    }

    @RequiredArgsConstructor
    private static class DirectionNode {
        private final Grid grid;
        @Getter
        private final Direction direction;

        private List<SetValueNode> children;
        private Optional<Double> score = Optional.empty();

        // TODO Pass Map<Grid, Node>
        public List<DirectionNode> buildChildren(Map<Grid, SetValueNode> nodesByGrid) {
            final List<DirectionNode> subNodes = new ArrayList<>();
            this.children = simulateValues(grid).stream()
                    .map(g -> {
                        var directionNodes = simulateDirections(g);
                        subNodes.addAll(directionNodes);
                        var child = new SetValueNode(g, directionNodes);
                        nodesByGrid.put(g, child);
                        return child;
                    })
                    .toList();
            return subNodes;
        }

        public double evaluateScore() {
            if (this.score.isPresent())
                return this.score.get();

            if (this.children == null || this.children.isEmpty())
                return scoreGrid(this.grid);

            return this.children.stream()
                    .mapToDouble(SetValueNode::getScore)
                    // TODO Better than average ?
                    .average().orElseThrow();
        }

        public void resetScore() {
            this.score = Optional.empty();
        }
    }

    private record SetValueNode(Grid grid, List<DirectionNode> children) {
        public double getScore() {
            return this.children.stream().mapToDouble(DirectionNode::evaluateScore).max()
                    .orElseGet(() -> scoreGrid(this.grid));
        }
    }
}
