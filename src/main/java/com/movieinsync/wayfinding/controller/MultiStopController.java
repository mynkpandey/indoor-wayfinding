package com.movieinsync.wayfinding.controller;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.MultiStopRequest;
import com.movieinsync.wayfinding.model.Node;

import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class MultiStopController {

    private final DijkstraService dijkstraService;
    private final Graph graph;

    public MultiStopController(
            DijkstraService dijkstraService,
            Graph graph) {

        this.dijkstraService = dijkstraService;
        this.graph = graph;
    }

    @PostMapping("/multi-stop")
    public Map<String, Object> findMultiStopRoute(
            @RequestBody MultiStopRequest request) {

        if (request.getStart() == null ||
                graph.getNode(request.getStart()) == null) {

            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "Start location does not exist."
            );
        }

        if (request.getStops() == null ||
                request.getStops().isEmpty()) {

            return Map.of(
                    "status", "INVALID_REQUEST",
                    "message", "At least one stop is required."
            );
        }

        for (String stop : request.getStops()) {

            if (graph.getNode(stop) == null) {
                return Map.of(
                        "status", "INVALID_REQUEST",
                        "message", "Stop does not exist: " + stop
                );
            }
        }

        String current = request.getStart();

        List<String> remainingStops =
                new ArrayList<>(request.getStops());

        List<String> fullPath = new ArrayList<>();
        fullPath.add(current);

        double totalDistance = 0.0;

        while (!remainingStops.isEmpty()) {

            String nearestStop = null;
            List<String> nearestPath = null;
            double nearestDistance = Double.POSITIVE_INFINITY;

            for (String stop : remainingStops) {

                List<String> path =
                        dijkstraService.findShortestPath(
                                current,
                                stop,
                                request.isWheelchairAccessible(),
                                LocalTime.now()
                        );

                if (path.isEmpty()) {
                    continue;
                }

                double distance = calculateDistance(path);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestStop = stop;
                    nearestPath = path;
                }
            }

            if (nearestStop == null) {
                return Map.of(
                        "status", "NO_ROUTE",
                        "message",
                        "One or more stops are unreachable."
                );
            }

            for (int i = 1; i < nearestPath.size(); i++) {
                fullPath.add(nearestPath.get(i));
            }

            totalDistance += nearestDistance;
            current = nearestStop;
            remainingStops.remove(nearestStop);
        }

        List<String> locations = new ArrayList<>();

        for (String nodeId : fullPath) {

            Node node = graph.getNode(nodeId);

            if (node != null) {
                locations.add(node.getName());
            }
        }

        return Map.of(
                "status", "SUCCESS",
                "path", locations,
                "totalDistance", totalDistance,
                "stopsVisited", request.getStops()
        );
    }

    private double calculateDistance(List<String> path) {

        double total = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {

            String current = path.get(i);
            String next = path.get(i + 1);

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