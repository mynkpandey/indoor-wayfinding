package com.movieinsync.wayfinding.config;

import com.movieinsync.wayfinding.algorithm.Graph;
import com.movieinsync.wayfinding.service.GraphRecoveryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphBackupInitializer {

    @Bean
    public CommandLineRunner createGraphBackup(
            Graph graph,
            GraphRecoveryService graphRecoveryService) {

        return args -> {
            graphRecoveryService.createBackup(graph);
        };
    }
}