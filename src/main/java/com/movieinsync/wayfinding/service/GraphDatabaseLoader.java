package com.movieinsync.wayfinding.service;

import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.entity.NodeEntity;
import com.movieinsync.wayfinding.model.Node;
import com.movieinsync.wayfinding.repository.NodeRepository;

import org.springframework.stereotype.Service;

@Service
public class GraphDatabaseLoader {

    private final NodeRepository nodeRepository;
    private final Graph graph;

    public GraphDatabaseLoader(
            NodeRepository nodeRepository,
            Graph graph) {

        this.nodeRepository = nodeRepository;
        this.graph = graph;
    }

    public void loadNodesIntoGraph() {

        for (NodeEntity entity : nodeRepository.findAll()) {

            Node node = new Node(
                    entity.getId(),
                    entity.getName(),
                    entity.getType(),
                    entity.getFloor()
            );

            graph.addNode(node);
        }
    }
}