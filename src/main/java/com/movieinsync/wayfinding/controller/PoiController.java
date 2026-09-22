package com.movieinsync.wayfinding.controller;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Node;
import java.time.LocalTime;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/poi")
public class PoiController {

    private final DijkstraService dijkstraService;
    private final Graph graph;

    public PoiController(DijkstraService dijkstraService, Graph graph) {
        this.dijkstraService = dijkstraService;
        this.graph = graph;
    }

    @GetMapping("/nearest")
    public Map<String, Object> findNearestPoi(
            @RequestParam String start,
            @RequestParam String type) {

        if (graph.getNode(start) == null) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Start location does not exist."
            );
        }

        double nearestDistance = Double.POSITIVE_INFINITY;
        Node nearestPoi = null;
        List<String> nearestPath = Collections.emptyList();

        for (Node node : graph.getAllNodes()) {

            if (!node.getType().equalsIgnoreCase(type)) {
                continue;
            }

            List<String> path = dijkstraService.findShortestPath(
                start,
                node.getId(),
                false,
                LocalTime.now()
            );

            if (path.isEmpty()) {
                continue;
            }

            double distance = calculateDistance(path);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPoi = node;
                nearestPath = path;
            }
        }

        if (nearestPoi == null) {
            return Map.of(
                    "status", "NOT_FOUND",
                    "message", "No reachable POI of the requested type was found."
            );
        }

        List<String> locations = new ArrayList<>();

        for (String nodeId : nearestPath) {
            Node node = graph.getNode(nodeId);

            if (node != null) {
                locations.add(node.getName());
            }
        }

        return Map.of(
                "status", "SUCCESS",
                "poi", nearestPoi.getName(),
                "type", nearestPoi.getType(),
                "path", locations,
                "distance", nearestDistance
        );
    }

    private double calculateDistance(List<String> path) {

        double total = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {

            String current = path.get(i);
            String next = path.get(i + 1);

            graph.getEdges(current).stream()
                    .filter(edge -> edge.getTo().equals(next))
                    .findFirst()
                    .ifPresent(edge -> {
                        // Distance calculation handled below
                    });

            for (var edge : graph.getEdges(current)) {
                if (edge.getTo().equals(next)) {
                    total += edge.getEffectiveDistance();
                    break;
                }
            }
        }

        return total;
    }
}