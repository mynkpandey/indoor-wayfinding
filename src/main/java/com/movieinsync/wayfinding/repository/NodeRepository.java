package com.movieinsync.wayfinding.repository;

import com.movieinsync.wayfinding.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<NodeEntity, String> {
}