package com.movieinsync.wayfinding.service;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import com.movieinsync.wayfinding.model.RouteRequest;
import com.movieinsync.wayfinding.model.RouteSegment;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalTime;
import java.util.*;

@Service
public class RouteService {

    private final DijkstraService dijkstraService;
    private final Graph graph;

    public RouteService(
            DijkstraService dijkstraService,
            Graph graph) {

        this.dijkstraService = dijkstraService;
        this.graph = graph;
    }
    @Cacheable(
        value = "routes",
        key = "#request.start + '-' + #request.destination + '-' + #request.wheelchairAccessible"
    )

    public Map<String, Object> findRoute(RouteRequest request) {

        if (request.getStart() == null ||
                request.getStart().isBlank()) {

            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Start location is required."
            );
        }

        if (request.getDestination() == null ||
                request.getDestination().isBlank()) {

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
                    "segments", List.of(),
                    "totalDistance", 0.0,
                    "estimatedTimeMinutes", 0.0
            );
        }

        List<String> path =
                dijkstraService.findShortestPath(
                        request.getStart(),
                        request.getDestination(),
                        request.isWheelchairAccessible(),
                        LocalTime.now()
                );

        if (path.isEmpty()) {

            return Map.of(
                    "status", "NO_ROUTE",
                    "message",
                    "No route found between the selected locations."
            );
        }

        double totalDistance = calculateDistance(path);

        double walkingSpeed = 1.4;

        double totalTimeSeconds =
                totalDistance / walkingSpeed;

        double totalTimeMinutes =
                totalTimeSeconds / 60.0;

        List<String> locations = new ArrayList<>();

        for (String nodeId : path) {

            Node node = graph.getNode(nodeId);

            if (node != null) {
                locations.add(node.getName());
            }
        }

        List<RouteSegment> segments =
                buildSegments(path);

        return Map.of(
                "status", "SUCCESS",
                "path", locations,
                "segments", segments,
                "totalDistance", totalDistance,
                "estimatedTimeMinutes",
                Math.round(totalTimeMinutes * 10.0) / 10.0
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

    private List<RouteSegment> buildSegments(List<String> path) {

        List<RouteSegment> segments = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {

            String current = path.get(i);
            String next = path.get(i + 1);

            Node fromNode = graph.getNode(current);
            Node toNode = graph.getNode(next);

            for (Edge edge : graph.getEdges(current)) {

                if (edge.getTo().equals(next)) {

                    RouteSegment segment =
                            new RouteSegment(
                                    fromNode.getName(),
                                    toNode.getName(),
                                    edge.getEffectiveDistance(),
                                    edge.isWheelchairAccessible()
                            );

                    segments.add(segment);
                    break;
                }
            }
        }

        return segments;
    }
}