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
}