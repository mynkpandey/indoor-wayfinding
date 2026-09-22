package com.movieinsync.wayfinding.config;

import com.movieinsync.wayfinding.entity.NodeEntity;
import com.movieinsync.wayfinding.repository.NodeRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedNodes(NodeRepository nodeRepository) {

        return args -> {

            if (nodeRepository.count() > 0) {
                return;
            }

            nodeRepository.save(
                    new NodeEntity("N1", "Reception", "RECEPTION", 1)
            );

            nodeRepository.save(
                    new NodeEntity("N2", "Corridor A", "CORRIDOR", 1)
            );

            nodeRepository.save(
                    new NodeEntity("N3", "Stairs", "STAIRS", 1)
            );

            nodeRepository.save(
                    new NodeEntity("N4", "Lift", "LIFT", 1)
            );

            nodeRepository.save(
                    new NodeEntity("N5", "Floor 2 Corridor", "CORRIDOR", 2)
            );

            nodeRepository.save(
                    new NodeEntity("N6", "Meeting Room 4B", "ROOM", 2)
            );

            nodeRepository.save(
                    new NodeEntity(
                            "N7",
                            "Washroom - Floor 1",
                            "WASHROOM",
                            1
                    )
            );

            nodeRepository.save(
                    new NodeEntity(
                            "N8",
                            "Water Station - Floor 2",
                            "WATER",
                            2
                    )
            );

            nodeRepository.save(
                    new NodeEntity(
                            "N9",
                            "Main Exit",
                            "EXIT",
                            1
                    )
            );
        };
    }
}