package com.movieinsync.wayfinding.repository;

import com.movieinsync.wayfinding.entity.EdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<EdgeEntity, Long> {

    List<EdgeEntity> findByFromNodeId(String fromNodeId);

}