package com.movieinsync.wayfinding.service;

import com.movieinsync.wayfinding.algorithm.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GraphRecoveryService {

    private static final Logger logger =
            LoggerFactory.getLogger(GraphRecoveryService.class);

    private Graph backupGraph;

    /**
     * Creates an independent backup of the campus graph.
     */
    public void createBackup(Graph graph) {

        if (graph == null) {
            logger.error("Cannot create graph backup: graph is null.");
            return;
        }

        backupGraph = graph.deepCopy();

        logger.info("Campus graph backup created successfully.");
    }

    /**
     * Recovers the campus graph from the backup.
     */
    public Graph recoverGraph() {

        if (backupGraph == null) {
            logger.error("Graph recovery failed: backup is unavailable.");
            return null;
        }

        logger.warn("Recovering campus graph from backup.");

        return backupGraph.deepCopy();
    }

    /**
     * Checks whether a backup is available.
     */
    public boolean isBackupAvailable() {
        return backupGraph != null;
    }
}