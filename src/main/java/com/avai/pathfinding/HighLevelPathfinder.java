package com.avai.pathfinding;

import net.minecraft.core.BlockPos;

import java.util.*;

public class HighLevelPathfinder {
    public static List<Region> findPath(RegionGraph graph, BlockPos start, BlockPos goal) {
        Region startRegion = graph.getRegion(start);
        Region goalRegion = graph.getRegion(goal);
        if (startRegion == null || goalRegion == null) return Collections.emptyList();

        Map<Region, Node> openSet = new HashMap<>();
        Map<Region, Node> closedSet = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));

        Node startNode = new Node(startRegion, null, 0, heuristic(startRegion, goalRegion));
        openSet.put(startRegion, startNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.region == goalRegion) {
                return reconstructPath(current);
            }
            openSet.remove(current.region);
            closedSet.put(current.region, current);

            for (RegionEdge edge : current.region.getEdges()) {
                Region neighbor = edge.getTo();
                if (closedSet.containsKey(neighbor)) continue;

                double tentativeG = current.gScore + edge.getCost();
                Node neighborNode = openSet.get(neighbor);
                if (neighborNode == null) {
                    neighborNode = new Node(neighbor, current, tentativeG, heuristic(neighbor, goalRegion));
                    openSet.put(neighbor, neighborNode);
                    queue.add(neighborNode);
                } else if (tentativeG < neighborNode.gScore) {
                    neighborNode.cameFrom = current;
                    neighborNode.gScore = tentativeG;
                    neighborNode.fScore = tentativeG + heuristic(neighbor, goalRegion);
                    queue.add(neighborNode); // re-add for priority update
                }
            }
        }
        return Collections.emptyList();
    }

    private static double heuristic(Region a, Region b) {
        BlockPos ca = a.getCenter();
        BlockPos cb = b.getCenter();
        return Math.sqrt(ca.distSqr(cb));
    }

    private static List<Region> reconstructPath(Node node) {
        LinkedList<Region> path = new LinkedList<>();
        while (node != null) {
            path.addFirst(node.region);
            node = node.cameFrom;
        }
        return path;
    }

    private static class Node {
        final Region region;
        Node cameFrom;
        double gScore;
        double fScore;

        Node(Region region, Node cameFrom, double g, double h) {
            this.region = region;
            this.cameFrom = cameFrom;
            this.gScore = g;
            this.fScore = g + h;
        }
    }
}