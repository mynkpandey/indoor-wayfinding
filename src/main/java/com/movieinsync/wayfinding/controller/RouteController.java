package com.movieinsync.wayfinding.controller;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import com.movieinsync.wayfinding.model.RouteRequest;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final DijkstraService dijkstraService;
    private final Graph graph;

    public RouteController(
            DijkstraService dijkstraService,
            Graph graph) {

        this.dijkstraService = dijkstraService;
        this.graph = graph;
    }

    @PostMapping
    public Map<String, Object> findRoute(@RequestBody RouteRequest request) {

        if (request.getStart() == null || request.getStart().isBlank()) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Start location is required."
            );
        }

        if (request.getDestination() == null || request.getDestination().isBlank()) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Destination is required."
            );
        }

        if (graph.getNode(request.getStart()) == null) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Start location does not exist."
            );
        }

        if (graph.getNode(request.getDestination()) == null) {
            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Destination does not exist."
            );
        }

        if (request.getStart().equals(request.getDestination())) {
            return Map.of(
                    "status", "SUCCESS",
                    "message", "Start and destination are the same.",
                    "path", List.of(
                            graph.getNode(request.getStart()).getName()
                    ),
                    "totalDistance", 0.0
            );
        }

        List<String> path = dijkstraService.findShortestPath(
                request.getStart(),
                request.getDestination(),
                request.isWheelchairAccessible()
        );

        if (path.isEmpty()) {
            return Map.of(
                    "status", "NO_ROUTE",
                    "message", "No route found between the selected locations."
            );
        }

        double totalDistance = calculateDistance(path);
        double walkingSpeed = 1.4; // meters per second
        double totalTimeSeconds = totalDistance / walkingSpeed;
        double totalTimeMinutes = totalTimeSeconds / 60.0;

        List<String> locations = new ArrayList<>();

        for (String nodeId : path) {
            Node node = graph.getNode(nodeId);

            if (node != null) {
                locations.add(node.getName());
            }
        }

        return Map.of(
                "status", "SUCCESS",
                "path", locations,
                "totalDistance", totalDistance,
                "estimatedTimeMinutes", Math.round(totalTimeMinutes * 10.0) / 10.0
        );
    }

    private double calculateDistance(List<String> path) {

        double total = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {

            String current = path.get(i);
            String next = path.get(i + 1);

            for (Edge edge : graph.getEdges(current)) {

                if (edge.getTo().equals(next)) {
                    total += edge.getEffectiveDistance();
                    break;
                }
            }
        }

        return total;
    }
}