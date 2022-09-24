package fr.rossi.game2048.players.ai;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import fr.rossi.game2048.Grid;
import fr.rossi.game2048.Move.Direction;
import fr.rossi.game2048.players.Player;
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
                .orElseGet(() -> DirectionNode.simulateDirections(grid));

        var total = grid.total();
        final List<Grid> gridsToRemove = this.nodesByGrid.keySet()
                .stream().filter(g -> g.total() <= total)
                .toList();
        gridsToRemove.forEach(nodesByGrid::remove); // TODO Do not clear?

        var maxNodes = Math.max(1, 10 - grid.getEmpty().size()) * 500;
        try (var context = new ExplorationContext(rootNodes, maxNodes)) {
            new Explorer(context, nodesByGrid).explore();
        }

        return rootNodes.stream()
                .sorted(Comparator.comparing(node -> -node.evaluateScore()))
                .findFirst().map(DirectionNode::getDirection).orElseThrow();
    }

    private class ExplorationContext implements Closeable {
        private List<DirectionNode> inProgress;
        private final Set<DirectionNode> allNodes;
        private final List<DirectionNode> nodesToBuild;
        private final long startTime;
        private final int maxNodesBySession;
        private int sessionNodesCount;

        ExplorationContext(List<DirectionNode> rootNodes, int maxNodesBySession) {
            this.inProgress = new ArrayList<>();
            // this.allNodes = Collections.synchronizedSet(new HashSet<>());
            this.allNodes = new HashSet<>(rootNodes); // TODO ???
            // this.nodesToBuild = Collections.synchronizedList(new ArrayList<>(rootNodes));
            this.nodesToBuild = new ArrayList<>(rootNodes);
            this.startTime = System.currentTimeMillis();
            this.maxNodesBySession = maxNodesBySession;
            this.sessionNodesCount = 0;
        }

        synchronized Optional<DirectionNode> pop() {
            if (this.nodesToBuild.isEmpty())
                return Optional.empty();

            this.sessionNodesCount++;
            var node = nodesToBuild.remove(0);
            this.inProgress.add(node);
            return Optional.of(node);
        }

        synchronized void addNodes(DirectionNode root, List<DirectionNode> nodes) {
            nodes.stream()
                    .filter(child -> !allNodes.contains(child))
                    .forEach(child -> {
                        allNodes.add(child);
                        nodesToBuild.add(child);
                    });
            this.inProgress.remove(root);
        }

        synchronized boolean ended() {
            return this.sessionNodesCount >= this.maxNodesBySession
                    || (this.inProgress.isEmpty() && this.nodesToBuild.isEmpty());
        }

        public void close() {
            var time = System.currentTimeMillis() - this.startTime;
            System.out.println(" -> time=" + time + "ms"
                    + " / avg=" + Math.round(time / (double) this.allNodes.size() * 1_000) + "µs"
                    + " / explored=" + this.allNodes.size()
                    + " / left=" + this.nodesToBuild.size());
            this.allNodes.forEach(DirectionNode::resetScore);
        }
    }

    @RequiredArgsConstructor
    private class Explorer {
        private final ExplorationContext context;
        private final Map<Grid, SetValueNode> nodesByGrid;

        void explore() {
            do {
                this.context.pop()
                        .ifPresent(node -> context.addNodes(node, node.buildChildren(this.nodesByGrid)));
            } while (!this.context.ended());
        }
    }

    @Override
    public void close() throws IOException {
        this.nodesByGrid.clear();
    }
}
