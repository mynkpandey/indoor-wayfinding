package com.movieinsync.wayfinding;

import com.movieinsync.wayfinding.algorithm.DijkstraService;
import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraServiceTest {

    private Graph createGraph() {

        Graph graph = new Graph();

        graph.addNode(new Node("A", "Start", "ROOM", 1));
        graph.addNode(new Node("B", "Corridor", "CORRIDOR", 1));
        graph.addNode(new Node("C", "Stairs", "STAIRS", 1));
        graph.addNode(new Node("D", "Lift", "LIFT", 1));
        graph.addNode(new Node("E", "Destination", "ROOM", 2));

        graph.addEdge(new Edge("A", "B", 10, true, false, 1.0));
        graph.addEdge(new Edge("B", "A", 10, true, false, 1.0));

        graph.addEdge(new Edge("B", "C", 10, false, false, 1.0));
        graph.addEdge(new Edge("C", "B", 10, false, false, 1.0));

        graph.addEdge(new Edge("B", "D", 15, true, false, 1.0));
        graph.addEdge(new Edge("D", "B", 15, true, false, 1.0));

        graph.addEdge(new Edge("C", "E", 10, false, false, 1.0));
        graph.addEdge(new Edge("E", "C", 10, false, false, 1.0));

        graph.addEdge(new Edge("D", "E", 20, true, false, 1.0));
        graph.addEdge(new Edge("E", "D", 20, true, false, 1.0));

        return graph;
    }

    @Test
    void shouldFindShortestPath() {

        Graph graph = createGraph();
        DijkstraService service = new DijkstraService(graph);

        List<String> path = service.findShortestPath(
                "A",
                "E",
                false,
                LocalTime.of(14, 0));

        assertEquals(List.of("A", "B", "C", "E"), path);
    }

    @Test
    void shouldAvoidStairsForWheelchairUsers() {

        Graph graph = createGraph();
        DijkstraService service = new DijkstraService(graph);

        List<String> path = service.findShortestPath(
                "A",
                "E",
                true,
                LocalTime.of(14, 0));

        assertEquals(List.of("A", "B", "D", "E"), path);
    }

    @Test
    void shouldReturnEmptyPathWhenDestinationIsUnreachable() {

        Graph graph = createGraph();

        DijkstraService service = new DijkstraService(graph);

        graph.addNode(new Node(
                "X",
                "Unreachable Room",
                "ROOM",
                3));

        List<String> path = service.findShortestPath(
                "A",
                "X",
                false,
                LocalTime.of(14, 0));

        assertTrue(path.isEmpty());
    }

    @Test
    void shouldPreferLessCongestedRoute() {

        Graph graph = new Graph();

        graph.addNode(new Node("A", "Start", "ROOM", 1));
        graph.addNode(new Node("B", "Congested Route", "CORRIDOR", 1));
        graph.addNode(new Node("C", "Alternative Route", "CORRIDOR", 1));
        graph.addNode(new Node("D", "Destination", "ROOM", 1));

        // Direct route: 10m × 2.0 congestion = 20 effective cost
        graph.addEdge(new Edge("A", "B", 10, true, false, 2.0));
        graph.addEdge(new Edge("B", "A", 10, true, false, 2.0));

        // Alternative route: 12m × 1.0 congestion = 12 effective cost
        graph.addEdge(new Edge("A", "C", 12, true, false, 1.0));
        graph.addEdge(new Edge("C", "A", 12, true, false, 1.0));

        graph.addEdge(new Edge("B", "D", 10, true, false, 1.0));
        graph.addEdge(new Edge("D", "B", 10, true, false, 1.0));

        graph.addEdge(new Edge("C", "D", 5, true, false, 1.0));
        graph.addEdge(new Edge("D", "C", 5, true, false, 1.0));

        DijkstraService service = new DijkstraService(graph);

        List<String> path = service.findShortestPath(
                "A",
                "D",
                false,
                LocalTime.of(14, 0));

        assertEquals(List.of("A", "C", "D"), path);
    }

    @Test
    void shouldAvoidClosedEdge() {

        Graph graph = new Graph();

        graph.addNode(new Node("A", "Start", "ROOM", 1));
        graph.addNode(new Node("B", "Corridor", "CORRIDOR", 1));
        graph.addNode(new Node("C", "Alternative", "CORRIDOR", 1));
        graph.addNode(new Node("D", "Destination", "ROOM", 1));

        // Closed from 13:00 to 15:00
        graph.addEdge(new Edge(
                "A",
                "B",
                5,
                true,
                false,
                1.0,
                LocalTime.of(13, 0),
                LocalTime.of(15, 0)));

        graph.addEdge(new Edge(
                "B",
                "A",
                5,
                true,
                false,
                1.0,
                LocalTime.of(13, 0),
                LocalTime.of(15, 0)));

        // Alternative route
        graph.addEdge(new Edge("A", "C", 10, true, false, 1.0));
        graph.addEdge(new Edge("C", "A", 10, true, false, 1.0));

        graph.addEdge(new Edge("C", "D", 10, true, false, 1.0));
        graph.addEdge(new Edge("D", "C", 10, true, false, 1.0));

        graph.addEdge(new Edge("B", "D", 5, true, false, 1.0));
        graph.addEdge(new Edge("D", "B", 5, true, false, 1.0));

        DijkstraService service = new DijkstraService(graph);

        List<String> path = service.findShortestPath(
                "A",
                "D",
                false,
                LocalTime.of(14, 0));

        assertEquals(List.of("A", "C", "D"), path);
    }
}