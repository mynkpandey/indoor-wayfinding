package com.movieinsync.wayfinding.config;

import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.model.Edge;
import com.movieinsync.wayfinding.model.Node;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public Graph campusGraph() {

        Graph graph = new Graph();

        // =========================
        // Create Campus Nodes
        // =========================

        Node reception =
                new Node("N1", "Reception", "RECEPTION", 1);

        Node corridorA =
                new Node("N2", "Corridor A", "CORRIDOR", 1);

        Node stairs =
                new Node("N3", "Stairs", "STAIRS", 1);

        Node lift =
                new Node("N4", "Lift", "LIFT", 1);

        Node floor2Corridor =
                new Node("N5", "Floor 2 Corridor", "CORRIDOR", 2);

        Node meetingRoom =
                new Node("N6", "Meeting Room 4B", "ROOM", 2);

        Node washroom = new Node("N7", "Washroom - Floor 1", "WASHROOM", 1);
        Node waterStation = new Node("N8", "Water Station - Floor 2", "WATER", 2);
        Node exit = new Node("N9", "Main Exit", "EXIT", 1);

        // =========================
        // Add Nodes to Graph
        // =========================

        graph.addNode(reception);
        graph.addNode(corridorA);
        graph.addNode(stairs);
        graph.addNode(lift);
        graph.addNode(floor2Corridor);
        graph.addNode(meetingRoom);
        graph.addNode(washroom);
        graph.addNode(waterStation);
        graph.addNode(exit);

        // =========================
        // Add Connections
        // =========================

        addTwoWayEdge(graph, "N1", "N2", 20, true);

        addTwoWayEdge(graph, "N2", "N3", 15, false);

        addTwoWayEdge(graph, "N2", "N4", 10, true);

        addTwoWayEdge(graph, "N3", "N5", 25, false);

        addTwoWayEdge(graph, "N4", "N5", 30, true);

        addTwoWayEdge(graph, "N5", "N6", 15, true);

        addTwoWayEdge(graph, "N2", "N7", 8, true);
        addTwoWayEdge(graph, "N5", "N8", 6, true);
        addTwoWayEdge(graph, "N1", "N9", 5, true);

        return graph;
    }

    private void addTwoWayEdge(
            Graph graph,
            String from,
            String to,
            double distance,
            boolean wheelchairAccessible) {

        graph.addEdge(
                new Edge(
                        from,
                        to,
                        distance,
                        wheelchairAccessible,
                        false,
                        1.0
                )
        );

        graph.addEdge(
                new Edge(
                        to,
                        from,
                        distance,
                        wheelchairAccessible,
                        false,
                        1.0
                )
        );
    }
}