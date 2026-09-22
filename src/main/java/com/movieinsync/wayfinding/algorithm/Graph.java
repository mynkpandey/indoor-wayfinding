package com.movieinsync.wayfinding.algorithm;

import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;

import java.util.*;

public class Graph {

    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        adjacencyList
                .computeIfAbsent(edge.getFrom(), k -> new ArrayList<>())
                .add(edge);
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public List<Edge> getEdges(String nodeId) {
        return adjacencyList.getOrDefault(
                nodeId,
                Collections.emptyList()
        );
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    /**
     * Creates an independent backup copy of the graph.
     */
    public Graph deepCopy() {

        Graph copy = new Graph();

        // Copy all nodes
        for (Node node : nodes.values()) {

            Node copiedNode = new Node(
                    node.getId(),
                    node.getName(),
                    node.getType(),
                    node.getFloor()
            );

            copy.addNode(copiedNode);
        }

        // Copy all edges
        for (List<Edge> edges : adjacencyList.values()) {

            for (Edge edge : edges) {

                Edge copiedEdge;

                if (edge.getClosedFrom() != null
                        && edge.getClosedUntil() != null) {

                    copiedEdge = new Edge(
                            edge.getFrom(),
                            edge.getTo(),
                            edge.getDistance(),
                            edge.isWheelchairAccessible(),
                            edge.isClosed(),
                            edge.getCongestionMultiplier(),
                            edge.getClosedFrom(),
                            edge.getClosedUntil()
                    );

                } else {

                    copiedEdge = new Edge(
                            edge.getFrom(),
                            edge.getTo(),
                            edge.getDistance(),
                            edge.isWheelchairAccessible(),
                            edge.isClosed(),
                            edge.getCongestionMultiplier()
                    );
                }

                copy.addEdge(copiedEdge);
            }
        }

        return copy;
    }
}