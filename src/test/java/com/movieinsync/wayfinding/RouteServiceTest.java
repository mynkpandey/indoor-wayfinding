package com.movieinsync.wayfinding;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import com.movieinsync.wayfinding.model.RouteRequest;
import com.movieinsync.wayfinding.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class RouteServiceTest {

    private RouteService routeService;

    @BeforeEach
    void setUp() {

        Graph graph = new Graph();

        graph.addNode(new Node(
                "A", "Reception", "ROOM", 1
        ));

        graph.addNode(new Node(
                "B", "Corridor", "CORRIDOR", 1
        ));

        graph.addNode(new Node(
                "C", "Destination", "ROOM", 1
        ));

        graph.addEdge(
                new Edge("A", "B", 10, true, false, 1.0)
        );

        graph.addEdge(
                new Edge("B", "A", 10, true, false, 1.0)
        );

        graph.addEdge(
                new Edge("B", "C", 20, true, false, 1.0)
        );

        graph.addEdge(
                new Edge("C", "B", 20, true, false, 1.0)
        );

        DijkstraService dijkstraService =
                new DijkstraService(graph);

        routeService =
                new RouteService(
                        dijkstraService,
                        graph
                );
    }

    @Test
    void shouldFindSuccessfulRoute() {

        RouteRequest request = new RouteRequest();

        request.setStart("A");
        request.setDestination("C");
        request.setWheelchairAccessible(false);

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "SUCCESS",
                response.get("status")
        );

        assertEquals(
                30.0,
                response.get("totalDistance")
        );

        assertNotNull(response.get("path"));
        assertNotNull(response.get("segments"));
    }

    @Test
    void shouldRejectMissingStart() {

        RouteRequest request = new RouteRequest();

        request.setStart("");
        request.setDestination("C");

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "INVALID_REQUEST",
                response.get("status")
        );

        assertEquals(
                "Start location is required.",
                response.get("message")
        );
    }

    @Test
    void shouldRejectMissingDestination() {

        RouteRequest request = new RouteRequest();

        request.setStart("A");
        request.setDestination("");

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "INVALID_REQUEST",
                response.get("status")
        );

        assertEquals(
                "Destination is required.",
                response.get("message")
        );
    }

    @Test
    void shouldRejectInvalidStart() {

        RouteRequest request = new RouteRequest();

        request.setStart("UNKNOWN");
        request.setDestination("C");

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "INVALID_REQUEST",
                response.get("status")
        );

        assertEquals(
                "Start location does not exist.",
                response.get("message")
        );
    }

    @Test
    void shouldRejectInvalidDestination() {

        RouteRequest request = new RouteRequest();

        request.setStart("A");
        request.setDestination("UNKNOWN");

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "INVALID_REQUEST",
                response.get("status")
        );

        assertEquals(
                "Destination does not exist.",
                response.get("message")
        );
    }

    @Test
    void shouldHandleSameStartAndDestination() {

        RouteRequest request = new RouteRequest();

        request.setStart("A");
        request.setDestination("A");

        Map<String, Object> response =
                routeService.findRoute(request);

        assertEquals(
                "SUCCESS",
                response.get("status")
        );

        assertEquals(
                0.0,
                response.get("totalDistance")
        );

        assertEquals(
                0.0,
                response.get("estimatedTimeMinutes")
        );
    }
}