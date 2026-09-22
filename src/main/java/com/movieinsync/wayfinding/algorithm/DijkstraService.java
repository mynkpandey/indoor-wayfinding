package com.movieinsync.wayfinding.algorithm;

import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class DijkstraService {

    private final Graph graph;

    public DijkstraService(Graph graph) {
        this.graph = graph;
    }

    public List<String> findShortestPath(
        String startId,
        String destinationId,
        boolean wheelchairAccessible) {

        // Distance from start node to every node
        Map<String, Double> distances = new HashMap<>();

        // Stores the previous node in the shortest path
        Map<String, String> previous = new HashMap<>();

        // PriorityQueue always gives us the node with the smallest distance
        PriorityQueue<NodeDistance> priorityQueue =
                new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::getDistance));

        // Initialize distances
        for (Node node : graph.getAllNodes()) {
            distances.put(node.getId(), Double.POSITIVE_INFINITY);
        }

        // Start node has distance 0
        distances.put(startId, 0.0);

        priorityQueue.add(new NodeDistance(startId, 0.0));

        while (!priorityQueue.isEmpty()) {

            NodeDistance current = priorityQueue.poll();

            String currentNode = current.getNodeId();
            double currentDistance = current.getDistance();

            // Ignore outdated entries
            if (currentDistance > distances.get(currentNode)) {
                continue;
            }

            // Destination reached
            if (currentNode.equals(destinationId)) {
                break;
            }

            // Check all connected edges
            for (Edge edge : graph.getEdges(currentNode)) {

                // Skip closed paths
                if (edge.isClosed()) {
                    continue;
                }

                if (wheelchairAccessible && !edge.isWheelchairAccessible()) {
                    continue;
                }

                String neighbour = edge.getTo();

                double newDistance =
                        currentDistance + edge.getEffectiveDistance();

                // Found a shorter path
                if (newDistance < distances.get(neighbour)) {

                    distances.put(neighbour, newDistance);

                    previous.put(neighbour, currentNode);

                    priorityQueue.add(
                            new NodeDistance(neighbour, newDistance)
                    );
                }
            }
        }

        // No route exists
        if (!distances.containsKey(destinationId)
                || distances.get(destinationId) == Double.POSITIVE_INFINITY) {

            return Collections.emptyList();
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();

        String current = destinationId;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);

        return path;
    }

    // Helper class for PriorityQueue
    private static class NodeDistance {

        private final String nodeId;
        private final double distance;

        public NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        public String getNodeId() {
            return nodeId;
        }

        public double getDistance() {
            return distance;
        }
    }
}